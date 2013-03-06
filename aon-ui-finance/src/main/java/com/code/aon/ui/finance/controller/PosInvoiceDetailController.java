package com.code.aon.ui.finance.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.finance.InvoiceDetail;
import com.code.aon.product.Item;

public class PosInvoiceDetailController extends SaleInvoiceDetailController {

	public void itemChanged(Item item) {
		super.itemChanged(item);
		fillTaxDataInDetail(false);
	}	

	public void quantityChanged(double quantity) {
		super.quantityChanged(quantity);
	}

	public void discountChanged(String discount) {
		super.discountChanged(discount);
	}

	public void onPlusQuantity(ActionEvent event) {
		InvoiceDetail invoiceDetail = (InvoiceDetail)getTo();
		if (invoiceDetail != null) {
			quantityChanged(invoiceDetail.getQuantity() + 1);
		}
	}

	public void onMinusQuantity(ActionEvent event) {
		InvoiceDetail invoiceDetail = (InvoiceDetail)getTo();
		if (invoiceDetail != null) {
			quantityChanged(invoiceDetail.getQuantity() - 1);
		}
	}

	@Override
	public void onAccept(ActionEvent event) {
		fillTaxDataInDetail(true);
		super.onAccept(event);
		onReset(event);
	}

}
