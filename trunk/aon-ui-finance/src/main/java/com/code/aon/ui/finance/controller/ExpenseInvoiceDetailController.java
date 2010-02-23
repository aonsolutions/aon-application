package com.code.aon.ui.finance.controller;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import com.code.aon.account.Account;
import com.code.aon.account.bridge.ProductAccount;
import com.code.aon.account.bridge.dao.IAccountBridgeAlias;
import com.code.aon.account.bridge.enumeration.ProductAccountType;
import com.code.aon.account.bridge.util.AccountBridgeUtil;
import com.code.aon.accounting.AccountHelper;
import com.code.aon.accounting.dao.IAccountingAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.product.Item;
import com.code.aon.product.dao.IProductAlias;
import com.code.aon.product.enumeration.ProductStatus;
import com.code.aon.product.enumeration.ProductType;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionUtilities;

public class ExpenseInvoiceDetailController extends InvoiceDetailController {

	private List<SelectItem> expenseItemList;

	public List<SelectItem> getExpenseItems() throws ManagerBeanException {
		return expenseItemList;
	}

	public void loadExpenseItems() throws ManagerBeanException {
		expenseItemList = getRecordedItemsByAccount();

		IManagerBean itemBean = BeanManager.getManagerBean(Item.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(itemBean.getFieldName(IProductAlias.ITEM_PRODUCT_TYPE), ProductType.EXPENSE);
		criteria.addEqualExpression(itemBean.getFieldName(IProductAlias.ITEM_STATUS), ProductStatus.ACTIVE);
		for (SelectItem to : expenseItemList) {
			Item item = (Item)to.getValue();
			if (item != null) {
				criteria.addExpression(ExpressionUtilities.getNotEqualExpression(itemBean.getFieldName(IProductAlias.ITEM_ID), item.getId()));
			}
		}
		criteria.addOrder(itemBean.getFieldName(IProductAlias.ITEM_PRODUCT_NAME));
		Iterator<?> iterator = itemBean.getList(criteria).iterator();
		while (iterator.hasNext()) {
			Item item = (Item)iterator.next();
			SelectItem selectItem = new SelectItem(item, item.getProduct().getName());
			expenseItemList.add(selectItem);
		}
	}

	private List<SelectItem> getRecordedItemsByAccount() throws ManagerBeanException {
		Invoice invoice = (Invoice)getMasterController().getTo();
		List<SelectItem> recordedItemList = new LinkedList<SelectItem>();
		AccountBridgeUtil accBridgeUtil = new AccountBridgeUtil();
		Account creditorAccount = accBridgeUtil.getCreditorAccount(invoice.getRegistry());
		if (creditorAccount != null) {
			IManagerBean helperBean = BeanManager.getManagerBean(AccountHelper.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(helperBean.getFieldName(IAccountingAlias.ACCOUNT_HELPER_ACCOUNT_ID), creditorAccount.getId());
			String balancingAlias = helperBean.getFieldName(IAccountingAlias.ACCOUNT_HELPER_BALANCING_ACCOUNT_ID);
			criteria.addExpression(ExpressionUtilities.getLikeExpression(balancingAlias, "6%"));	
			criteria.addOrder(helperBean.getFieldName(IAccountingAlias.ACCOUNT_HELPER_COUNTER), false);
			List<ITransferObject> helperList = helperBean.getList(criteria);
			for (ITransferObject helperTo : helperList) {
				AccountHelper helper = (AccountHelper) helperTo;

				IManagerBean pAccountBean = BeanManager.getManagerBean(ProductAccount.class);
				criteria = new Criteria();
				criteria.addEqualExpression(pAccountBean.getFieldName(IAccountBridgeAlias.PRODUCT_ACCOUNT_ACCOUNT_ID), helper.getBalancingAccount().getId());
				criteria.addEqualExpression(pAccountBean.getFieldName(IAccountBridgeAlias.PRODUCT_ACCOUNT_TYPE), ProductAccountType.PURCHASE);
				criteria.addOrder(pAccountBean.getFieldName(IAccountBridgeAlias.PRODUCT_ACCOUNT_PRODUCT_NAME));
				List<ITransferObject> pAccountList = pAccountBean.getList(criteria);
				for (ITransferObject pAccountTo : pAccountList) {
					ProductAccount pAccount = (ProductAccount) pAccountTo;
					
					IManagerBean itemBean = BeanManager.getManagerBean(Item.class);
					criteria = new Criteria();
					criteria.addEqualExpression(itemBean.getFieldName(IProductAlias.ITEM_PRODUCT_ID), pAccount.getProduct().getId());
					criteria.addEqualExpression(itemBean.getFieldName(IProductAlias.ITEM_PRODUCT_TYPE), ProductType.EXPENSE);
					criteria.addEqualExpression(itemBean.getFieldName(IProductAlias.ITEM_STATUS), ProductStatus.ACTIVE);
					List<ITransferObject> itemList = itemBean.getList(criteria);
					for (ITransferObject itemTo : itemList) {
						Item item = (Item) itemTo;
						SelectItem selectItem = new SelectItem(item, item.getProduct().getName());
						recordedItemList.add(selectItem);
					}
				}
			}
		}

		if (recordedItemList.size() > 0) {
			recordedItemList.add(new SelectItem(null, "------------------------------","------------------------------", true));
		}
		return recordedItemList;
	}

	public void onItemChanged(ValueChangeEvent event) {
		if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
			Item item = (Item)event.getNewValue();
			itemChanged(item);
		}
	}	

	public void itemChanged(Item item) {
		InvoiceDetail invoiceDetail = (InvoiceDetail) getTo();
		invoiceDetail.setItem(item);
		invoiceDetail.setDescription(item.getProduct().getName() + (item.getDetail() !=null ? " " + item.getDetail() : ""));
		invoiceDetail.setQuantity(1);
		invoiceDetail.setTaxableBase(item.getPurchasePrice());
		invoiceDetail.setVatPercent(item.getProduct().getVat() != null ? item.getProduct().getVat().getPercentage() : 0);
		invoiceDetail.setVatQuota(getVatQuota(invoiceDetail));
		invoiceDetail.setRetentionPercent(item.getProduct().getRetention() != null ? item.getProduct().getRetention().getPercentage() : 0);
		invoiceDetail.setRetentionQuota(getRetentionQuota(invoiceDetail));
	}

	public void onLongDescription(ActionEvent event) {
		setLongDescription(true);
	}

	public void onShortDescription(ActionEvent event) {
		setLongDescription(false);
	}

	public void onTaxableBaseChanged(ValueChangeEvent event) {
		InvoiceDetail invoiceDetail = (InvoiceDetail) getTo();
		if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
			Double taxableBase = (Double)event.getNewValue();
			invoiceDetail.setTaxableBase(taxableBase);
			invoiceDetail.setVatQuota(getVatQuota(invoiceDetail));
			invoiceDetail.setRetentionQuota(getRetentionQuota(invoiceDetail));
		}
	}

	public void onVatQuotaChanged(ValueChangeEvent event) {
		InvoiceDetail invoiceDetail = (InvoiceDetail) getTo();
		if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
			Double vatQuota = (Double)event.getNewValue();
			if (vatQuota == 0 && invoiceDetail.getVatPercent() != 0) {
				invoiceDetail.setVatPercent(0);
			}
		}
	}

	public void onRetentionQuotaChanged(ValueChangeEvent event) {
		InvoiceDetail invoiceDetail = (InvoiceDetail) getTo();
		if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
			Double retentionQuota = (Double)event.getNewValue();
			if (retentionQuota == 0 && invoiceDetail.getRetentionPercent() != 0) {
				invoiceDetail.setRetentionPercent(0);
			}
		}
	}

	public void onTotalChanged(ValueChangeEvent event) {
		InvoiceDetail invoiceDetail = (InvoiceDetail) getTo();
		if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
			double total = ((Double)event.getNewValue()).doubleValue();
			invoiceDetail.setTaxableBase(total / ( 1 + (invoiceDetail.getVatPercent() / 100) - (invoiceDetail.getRetentionPercent() / 100)));
			invoiceDetail.setVatQuota(getVatQuota(invoiceDetail));
			invoiceDetail.setRetentionQuota(getRetentionQuota(invoiceDetail));
			invoiceDetail.setTaxableBase(total - invoiceDetail.getVatQuota() + invoiceDetail.getRetentionQuota());
		}
	}

	private double getVatQuota(InvoiceDetail invoiceDetail) {
		return CommonUtil.round(invoiceDetail.getTaxableBase() * invoiceDetail.getVatPercent()/100);
	}

	private double getRetentionQuota(InvoiceDetail invoiceDetail) {
		return CommonUtil.round(invoiceDetail.getTaxableBase() * invoiceDetail.getRetentionPercent()/100);
	}

	public double getInvoiceDetailTotal() throws ManagerBeanException {
		InvoiceDetail invoiceDetail = (InvoiceDetail)this.getModel().getRowData();
		return getTotal(invoiceDetail);
	}

	public double getToInvoiceDetailTotal() {
		InvoiceDetail invoiceDetail = (InvoiceDetail)this.getTo();
		return getTotal(invoiceDetail);
	}

	public void setToInvoiceDetailTotal(double toInvoiceDetailTotal) {
	}

	private double getTotal(InvoiceDetail invoiceDetail) {
		return CommonUtil.round(invoiceDetail.getTaxableBase() + invoiceDetail.getVatQuota() - invoiceDetail.getRetentionQuota());
	}

}
