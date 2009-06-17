package com.code.aon.ui.finance.controller;

import java.util.Date;

import javax.faces.event.ValueChangeEvent;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.Tariff;
import com.code.aon.customer.Customer;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.invoicing.pricing.InvoicePriceStrategy;
import com.code.aon.product.Item;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.form.LinesController;

public class SaleInvoiceDetailController extends LinesController {

	private IPriceStrategy priceStrategy;

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
			Item item = (Item)event.getNewValue();
			invoiceDetail.setItem(item);
			invoiceDetail.setDescription(item.getProduct().getName() + " " + (item.getDetail()!=null?item.getDetail():""));

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
	public double getTaxableBase() {
		InvoiceDetail invoiceDetail = (InvoiceDetail) getTo();
		return getPriceStrategy().getBasePrice(invoiceDetail);
	}

	public void onQuantityChanged(ValueChangeEvent event) {
		InvoiceDetail invoiceDetail = (InvoiceDetail) getTo();
		if (invoiceDetail.getItem() != null && invoiceDetail.getItem().getId() != null) {
			double price = 0;
			if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
				invoiceDetail.setQuantity((Double)event.getNewValue());
	
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
}
