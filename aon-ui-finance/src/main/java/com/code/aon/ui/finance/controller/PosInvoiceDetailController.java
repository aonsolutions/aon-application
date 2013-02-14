package com.code.aon.ui.finance.controller;

import javax.faces.event.ActionEvent;

public class PosInvoiceDetailController extends SaleInvoiceDetailController {

	@Override
	public void onAccept(ActionEvent event) {
		super.onAccept(event);
		if (((InvoiceController)getMasterController()).getFinanceGenerationMode() < 0) {
			onReset(event);
		}
	}

}
