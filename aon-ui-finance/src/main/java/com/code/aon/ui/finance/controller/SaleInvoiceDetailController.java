package com.code.aon.ui.finance.controller;

import javax.faces.event.ValueChangeEvent;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.Tariff;
import com.code.aon.config.Tax;
import com.code.aon.customer.Customer;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.product.Item;
import com.code.aon.ui.common.components.LookupChangeEvent;

public class SaleInvoiceDetailController extends InvoiceDetailController {

	public void onItemChanged(LookupChangeEvent event) throws ManagerBeanException {
		if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
			Item item = (Item)event.getNewValue();
			itemChanged(item);
		}
	}	

	public void itemChanged(Item item) throws ManagerBeanException {
		Invoice invoice = (Invoice)getMasterController().getTo();
		Tax vat = item.getProduct().getVat();
		Tax retention = item.getProduct().getRetention();
		InvoiceDetail invoiceDetail = (InvoiceDetail) getTo();
		invoiceDetail.setItem(item);
		invoiceDetail.setDescription(item.getProduct().getName() + (item.getDetail() != null ? " " + item.getDetail() : ""));
		if (invoiceDetail.getQuantity() == 0) {
			invoiceDetail.setQuantity(1);
		}
		if (invoice.getRegistry() != null && invoice.getRegistry().getId() != null) {
			Tariff tariff;
			try {
				Customer customer = (Customer)BeanManager.getManagerBean(Customer.class).get(invoice.getRegistry().getId());
				tariff = customer.getTariff();
			} catch (ManagerBeanException e) {
				tariff = null;
			}
			invoiceDetail.setPrice(getPriceStrategy().getUnitPrice(invoiceDetail, invoice.getIssueDate(), tariff));
		} else {
			invoiceDetail.setPrice(getPriceStrategy().getUnitPrice(invoiceDetail));
		}
		if (vat == null || invoiceDetail.getVatPercent() != vat.getPercentage()) {
			invoiceDetail.setVatPercent(vat != null ? getTaxPercent(vat, invoice.getIssueDate(), false) : 0);
		}
		if (retention == null || invoiceDetail.getRetentionPercent() != retention.getPercentage()) {
			invoiceDetail.setRetentionPercent(retention != null ? getTaxPercent(retention, invoice.getIssueDate(), false) : 0);
		}
	}	

	public void onQuantityChanged(ValueChangeEvent event) {
		Invoice invoice = (Invoice)getMasterController().getTo();
		InvoiceDetail invoiceDetail = (InvoiceDetail) getTo();
		if (invoiceDetail.getItem() != null && invoiceDetail.getItem().getId() != null) {
			if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
				invoiceDetail.setQuantity((Double)event.getNewValue());
				if (invoice.getRegistry() != null && invoice.getRegistry().getId() != null) {
					Tariff tariff;
					try {
						Customer customer = (Customer)BeanManager.getManagerBean(Customer.class).get(invoice.getRegistry().getId());
						tariff = customer.getTariff();
					} catch (ManagerBeanException e) {
						tariff = null;
					}
					invoiceDetail.setPrice(getPriceStrategy().getUnitPrice(invoiceDetail, invoice.getIssueDate(), tariff));
				} else {
					invoiceDetail.setPrice(getPriceStrategy().getUnitPrice(invoiceDetail));
				}
			} else {
				invoiceDetail.setPrice(0);
			}
		}
	}

}
