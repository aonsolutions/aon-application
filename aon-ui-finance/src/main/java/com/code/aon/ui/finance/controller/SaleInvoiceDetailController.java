package com.code.aon.ui.finance.controller;

import java.util.Date;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.faces.component.richfaces.lookup.LookupChangeEvent;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.invoicing.InvoicePriceStrategy;
import com.code.aon.product.Item;
import com.code.aon.product.Tariff;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.ui.form.LinesController;

public class SaleInvoiceDetailController extends LinesController {

	private IPriceStrategy priceStrategy;

	/*
	 * private WorkPlace workPlace;
	 * 
	 * public WorkPlace getWorkPlace() { return workPlace; }
	 * 
	 * public void setWorkPlace(WorkPlace workPlace) { this.workPlace =
	 * workPlace; }
	 * 
	 * @SuppressWarnings("unchecked") public void itemData(ValueChangeEvent
	 * event) throws ManagerBeanException, ExpressionException{
	 * if(event.getNewValue() != null && !event.getNewValue().equals("")){
	 * IManagerBean itemBean = BeanManager.getManagerBean(Item.class); Criteria
	 * criteria = new Criteria();
	 * criteria.addExpression(itemBean.getFieldName(IProductAlias.ITEM_ID),
	 * event.getNewValue().toString()); Iterator iter =
	 * itemBean.getList(criteria).iterator(); if(iter.hasNext()){ Item item =
	 * (Item)iter.next(); ((InvoiceDetail)this.getTo()).setItem(item); } } }
	 */
	public IPriceStrategy getPriceStrategy() {
		if (priceStrategy == null) {
			priceStrategy = new InvoicePriceStrategy();
		}
		return priceStrategy;
	}

	public void onItemChanged(LookupChangeEvent event) {
		InvoiceDetail invoiceDetail = (InvoiceDetail) getTo();
		double price = 0;
		if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
			invoiceDetail.setItem((Item)event.getNewValue());

			Date date = invoiceDetail.getInvoice().getIssueDate();
			Tariff tariff;
			try {
				SaleInvoiceController master = (SaleInvoiceController) getMasterController();
				Customer customer = master.getCustomer();
				tariff = customer.getTariff();
			} catch (ManagerBeanException e) {
				tariff = null;
			}
			price = getPriceStrategy().getUnitPrice(invoiceDetail, date, tariff);
		}
		invoiceDetail.setPrice(price);
	}
}