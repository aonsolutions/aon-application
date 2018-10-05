package com.code.aon.ui.finance.controller;

import javax.faces.event.ValueChangeEvent;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.product.Item;
import com.code.aon.supplier.Supplier;
import com.code.aon.ui.common.components.LookupChangeEvent;

public class PurchaseInvoiceDetailController extends InvoiceDetailController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	public void onItemChanged(LookupChangeEvent event) {
		if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
			Item item = (Item)event.getNewValue();
			itemChanged(item);
		} else {
			InvoiceDetail invoiceDetail = (InvoiceDetail) getTo();
			invoiceDetail.setQuantity(0);
			invoiceDetail.setPrice(0);
			invoiceDetail.getDiscountExpression().setDiscountExpr("0.0");
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

		Supplier supplier = null;
		if (invoice.getRegistry() != null && invoice.getRegistry().getId() != null) {
			try {
				supplier = (Supplier)BeanManager.getManagerBean(Supplier.class).get(invoice.getRegistry().getId());
			} catch (ManagerBeanException e) {
			}
		}
		invoiceDetail.setPrice(getPriceStrategy().getUnitPurchasePrice(invoiceDetail, invoice.getIssueDate(), supplier));
		fillTaxDataInDetail(false, true);
		
		if(this.isNevv() && item.getProduct().isComposition()) {
			this.getCompositeHandler().load(this, item);
		}
	}	

	public void onQuantityChanged(ValueChangeEvent event) {
		quantityChanged((event.getNewValue() != null && !event.getNewValue().toString().equals("")) ? (Double)event.getNewValue() : 0);
		fillTaxDataInDetail(false, true);
	}

	public void quantityChanged(double quantity) {
		Invoice invoice = (Invoice)getMasterController().getTo();
		InvoiceDetail invoiceDetail = (InvoiceDetail) getTo();
		invoiceDetail.setQuantity(quantity);
		if (invoiceDetail.getItem() != null && invoiceDetail.getItem().getId() != null) {
			Supplier supplier = null;
			if (invoice.getRegistry() != null && invoice.getRegistry().getId() != null) {
				try {
					supplier = (Supplier)BeanManager.getManagerBean(Supplier.class).get(invoice.getRegistry().getId());
				} catch (ManagerBeanException e) {
				}
			}
			invoiceDetail.setPrice(getPriceStrategy().getUnitPurchasePrice(invoiceDetail, invoice.getIssueDate(), supplier));
		} else {
			invoiceDetail.setPrice(0);
		}
	}

	public void onPriceChanged(ValueChangeEvent event) {
		priceChanged((event.getNewValue() != null && !event.getNewValue().toString().equals("")) ? (Double)event.getNewValue() : 0);
		fillTaxDataInDetail(false, true);
	}

	public void priceChanged(double price) {
		InvoiceDetail invoiceDetail = (InvoiceDetail) getTo();
		invoiceDetail.setPrice(price);
	}

	public void onDiscountChanged(ValueChangeEvent event) {
		discountChanged((event.getNewValue() != null && !event.getNewValue().toString().equals("")) ? event.getNewValue().toString() : "0.0");
		fillTaxDataInDetail(false, true);
	}

	public void discountChanged(String discount) {
		InvoiceDetail invoiceDetail = (InvoiceDetail) getTo();
		invoiceDetail.getDiscountExpression().setDiscountExpr(discount);
	}

}
