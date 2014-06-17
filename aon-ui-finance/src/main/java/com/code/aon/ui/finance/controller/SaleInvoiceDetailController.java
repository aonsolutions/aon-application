package com.code.aon.ui.finance.controller;

import javax.faces.event.ValueChangeEvent;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.product.Item;
import com.code.aon.ui.common.components.LookupChangeEvent;

public class SaleInvoiceDetailController extends InvoiceDetailController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	public void onItemChanged(LookupChangeEvent event) {
		if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
			Item item = (Item)event.getNewValue();
			itemChanged(item);
		}
	}	

	public void itemChanged(Item item) {
		Invoice invoice = (Invoice)getMasterController().getTo();
		InvoiceDetail invoiceDetail = (InvoiceDetail) getTo();
		invoiceDetail.setItem(item);
		invoiceDetail.setDescription(item.getFullName());
		if (invoiceDetail.getQuantity() == 0) {
			invoiceDetail.setQuantity(1);
		}
		Customer customer = null;
		if (invoice.getRegistry() != null && invoice.getRegistry().getId() != null) {
			try {
				customer = (Customer)BeanManager.getManagerBean(Customer.class).get(invoice.getRegistry().getId());
			} catch (ManagerBeanException e) {
			}
		}
		invoiceDetail.setPrice(getPriceStrategy().getUnitPrice(invoiceDetail, invoice.getIssueDate(), customer));
	}	

	public void onQuantityChanged(ValueChangeEvent event) {
		quantityChanged((event.getNewValue() != null && !event.getNewValue().toString().equals("")) ? (Double)event.getNewValue() : 0);
	}

	public void quantityChanged(double quantity) {
		Invoice invoice = (Invoice)getMasterController().getTo();
		InvoiceDetail invoiceDetail = (InvoiceDetail) getTo();
		invoiceDetail.setQuantity(quantity);
		if (invoiceDetail.getItem() != null && invoiceDetail.getItem().getId() != null) {
			Customer customer = null;
			if (invoice.getRegistry() != null && invoice.getRegistry().getId() != null) {
				try {
					customer = (Customer)BeanManager.getManagerBean(Customer.class).get(invoice.getRegistry().getId());
				} catch (ManagerBeanException e) {
				}
			}
			invoiceDetail.setPrice(getPriceStrategy().getUnitPrice(invoiceDetail, invoice.getIssueDate(), customer));
		} else {
			invoiceDetail.setPrice(0);
		}
	}

	public void onDiscountChanged(ValueChangeEvent event) {
		discountChanged((event.getNewValue() != null && !event.getNewValue().toString().equals("")) ? event.getNewValue().toString() : "0");
	}

	public void discountChanged(String discount) {
		InvoiceDetail invoiceDetail = (InvoiceDetail) getTo();
		invoiceDetail.getDiscountExpression().setDiscountExpr(discount);
	}

}
