package com.code.aon.ui.warehouse.controller;

import java.util.Iterator;

import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.InvoiceSource;
import com.code.aon.product.Item;
import com.code.aon.product.strategy.ICalculable;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.product.strategy.PriceStrategyFactory;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.warehouse.IncomeDetail;

public class IncomeDetailController extends LinesController {

	private boolean longDescription;

	private IPriceStrategy priceStrategy;
	
	public boolean isLongDescription() {
		return longDescription;
	}

	public void setLongDescription(boolean longDescription) {
		this.longDescription = longDescription;
	}

	public IPriceStrategy getPriceStrategy(){
		if(priceStrategy == null){
			priceStrategy = PriceStrategyFactory.getPriceStrategy();
		}
		return priceStrategy;
	}

	public void onLongDescription(ActionEvent event) {
		setLongDescription(true);

		IncomeDetail incomeDetail = (IncomeDetail)getTo();
		if (StringUtils.equals(incomeDetail.getItem().getProduct().getName().trim(), incomeDetail.getDescription().trim())) {
			String longDescription = incomeDetail.getItem().getDescription();
			if (!StringUtils.isEmpty(longDescription)) {
				incomeDetail.setDescription(incomeDetail.getDescription() + "\r\n" + longDescription);
			}
		}
	}

	public void onShortDescription(ActionEvent event) {
		setLongDescription(false);
	}

	public boolean isPurchaseSource() throws ManagerBeanException {
		IncomeDetail incomeDetail = (IncomeDetail)getTo();
		if (incomeDetail != null) {
			return (incomeDetail.getPurchaseDetail() != null && incomeDetail.getPurchaseDetail().getId() != null);
		}
		return false;
	}

	public void onItemChanged(LookupChangeEvent event) {
		IncomeDetail incomeDetail = (IncomeDetail)getTo();
		double price = 0;
		if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
			Item item = (Item)event.getNewValue();
			incomeDetail.setItem(item);
			incomeDetail.setDescription(item.getProduct().getName() + " " + (item.getDetail()!=null?item.getDetail():""));

			price = item.getPurchasePrice();
		}
		incomeDetail.setPrice(price);
	}	

	public double getAmount() {
		return getPriceStrategy().getBasePrice((ICalculable)this.getTo());
	}

	public double getModelAmount() throws ManagerBeanException {
		return getPriceStrategy().getBasePrice((ICalculable)this.getModel().getRowData());
	}

	public String getLineSourceInfo() throws ManagerBeanException {
		StringBuffer info = new StringBuffer(64);

		IncomeDetail incomeDetail = (IncomeDetail)this.getModel().getRowData();
		if (incomeDetail.getPurchaseDetail() != null && incomeDetail.getPurchaseDetail().getId() != null) {
			info.append(AonUtil.getMessage("warehouseBundle", "warehouse_income_source"));
			info.append(" ");
			info.append(AonUtil.getMessage("purchaseBundle", "purchase_purchase"));
			info.append(" ");
			info.append(incomeDetail.getPurchaseDetail().getPurchase().getReferenceCode());
			info.append(" - ");
			info.append(AonUtil.getMessage("warehouseBundle", "warehouse_income_detail_line"));
			info.append(" ");
			info.append(incomeDetail.getPurchaseDetail().getLine());
		}
		return info.toString();
	}

	public String getLineStatusInfo() throws ManagerBeanException {
		StringBuffer info = new StringBuffer(64);

		IncomeDetail incomeDetail = (IncomeDetail)this.getModel().getRowData();
		IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoiceDetailBean.getFieldName(IFinanceAlias.INVOICE_DETAIL_SOURCE), InvoiceSource.INCOME);
		criteria.addEqualExpression(invoiceDetailBean.getFieldName(IFinanceAlias.INVOICE_DETAIL_SOURCE_ID), incomeDetail.getId());
		Iterator<?> iterator = invoiceDetailBean.getList(criteria).iterator();
		if (iterator.hasNext()) {
			InvoiceDetail invoiceDetail = (InvoiceDetail)iterator.next();
			info.append(AonUtil.getMessage("warehouseBundle", "warehouse_income_transfered_to"));
			info.append(" ");
			info.append(AonUtil.getMessage("financeBundle", "finance_invoice"));
			info.append(" ");
			info.append(invoiceDetail.getInvoice().getReferenceCode());
			info.append(" - ");
			info.append(AonUtil.getMessage("warehouseBundle", "warehouse_income_detail_line"));
			info.append(" ");
			info.append(invoiceDetail.getLine());
		}
		return info.toString();
	}

}