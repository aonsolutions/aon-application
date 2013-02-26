package com.code.aon.ui.finance.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.product.Item;

public class PosInvoiceDetailController extends SaleInvoiceDetailController {

	public void itemChanged(Item item) throws ManagerBeanException {
		super.itemChanged(item);
		fillTaxDataInDetail();
	}	

	public void quantityChanged(double quantity) throws ManagerBeanException {
		super.quantityChanged(quantity);
		fillTaxDataInDetail();
	}

	public void discountChanged(String discount) throws ManagerBeanException {
		super.discountChanged(discount);
		fillTaxDataInDetail();
	}

	@Override
	public void onAccept(ActionEvent event) {
		super.onAccept(event);
		onReset(event);
	}

}
