package com.code.aon.ui.finance.controller;

import java.util.Date;

import javax.faces.event.ValueChangeEvent;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.Tariff;
import com.code.aon.customer.Customer;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.product.Item;
import com.code.aon.ui.common.components.LookupChangeEvent;

public class SaleInvoiceDetailController extends InvoiceDetailController {

	public void onItemChanged(LookupChangeEvent event) {
		InvoiceDetail invoiceDetail = (InvoiceDetail) getTo();
		double price = 0;
		if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
			Item item = (Item)event.getNewValue();
			invoiceDetail.setItem(item);
			invoiceDetail.setDescription(item.getProduct().getName() + (item.getDetail() != null ? " " + item.getDetail() : ""));
			if (invoiceDetail.getQuantity() == 0) {
				invoiceDetail.setQuantity(1);
			}

			Date date = invoiceDetail.getInvoice().getIssueDate();
			Tariff tariff;
			try {
				SaleInvoiceController master = (SaleInvoiceController) getMasterController();
				date = ((Invoice)master.getTo()).getIssueDate();
				Customer customer = master.getCustomer();
				tariff = customer.getTariff();
			} catch (ManagerBeanException e) {
				tariff = null;
			}
			price = getPriceStrategy().getUnitPrice(invoiceDetail, date, tariff);
		}
		invoiceDetail.setPrice(price);
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
					date = ((Invoice)master.getTo()).getIssueDate();
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
