package com.code.aon.ui.finance.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.finance.Finance;
import com.code.aon.ui.form.FormUtil;

public class PosInvoiceController extends SaleInvoiceController {

	private boolean showFinishTicketWindow;

	public PosInvoiceController() {
		setInvoiceAddressControllerName(POS_INVOICE_ADDRESS_CONTROLLER_NAME);
		setInvoiceDetailControllerName(POS_INVOICE_DETAIL_CONTROLLER_NAME);
		setInvoiceFinanceControllerName(POS_INVOICE_FINANCE_CONTROLLER_NAME);
	}

	public boolean isShowFinishTicketWindow() {
		return showFinishTicketWindow;
	}

	public void setShowFinishTicketWindow(boolean value) {
		this.showFinishTicketWindow = value;
	}

	public void onFinishTicket(ActionEvent event) {
		InvoiceFinanceController financeController = (InvoiceFinanceController)FormUtil.getController(getInvoiceFinanceControllerName());
		if (financeController.getPaidAmount() < getInvoice().getTotal() && financeController.getPaidAmount() > 0) {
			((Finance)financeController.getTo()).setAmount(financeController.getPaidAmount());
		}
		financeController.onAccept(event);
	}

}