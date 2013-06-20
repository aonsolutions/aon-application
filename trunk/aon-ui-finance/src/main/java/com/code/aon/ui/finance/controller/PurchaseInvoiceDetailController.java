package com.code.aon.ui.finance.controller;

import javax.faces.event.ValueChangeEvent;

import com.code.aon.finance.InvoiceDetail;
import com.code.aon.product.Item;
import com.code.aon.ui.common.components.LookupChangeEvent;

public class PurchaseInvoiceDetailController extends InvoiceDetailController {

	public void onItemChanged(LookupChangeEvent event) {
		InvoiceDetail invoiceDetail = (InvoiceDetail) getTo();
		double price = 0;
		if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
			Item item = (Item)event.getNewValue();
			invoiceDetail.setItem(item);
			invoiceDetail.setDescription(item.getFullName());
			if (invoiceDetail.getQuantity() == 0) {
				invoiceDetail.setQuantity(1);
			}

			price = item.getPurchasePrice();
		}
		invoiceDetail.setPrice(price);
	}	

	public void onQuantityChanged(ValueChangeEvent event) {
	}

}
