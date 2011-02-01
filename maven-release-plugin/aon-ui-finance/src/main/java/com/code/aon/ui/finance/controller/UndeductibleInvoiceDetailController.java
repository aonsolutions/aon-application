package com.code.aon.ui.finance.controller;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

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
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.product.Item;
import com.code.aon.product.dao.IProductAlias;
import com.code.aon.product.enumeration.ProductStatus;
import com.code.aon.product.enumeration.ProductType;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionUtilities;

public class UndeductibleInvoiceDetailController extends InvoiceDetailController {

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
	}

}
