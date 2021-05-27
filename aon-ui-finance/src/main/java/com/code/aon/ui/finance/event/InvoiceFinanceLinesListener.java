package com.code.aon.ui.finance.event;

import com.code.aon.AonVersion;
import com.code.aon.finance.Finance;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.ui.finance.controller.InvoiceController;
import com.code.aon.ui.finance.controller.InvoiceDetailController;
import com.code.aon.ui.finance.controller.InvoiceFinanceController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.form.listener.LinesControllerListener;

public class InvoiceFinanceLinesListener extends LinesControllerListener {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		InvoiceFinanceController financeController = (InvoiceFinanceController)getLinesController();
		InvoiceController invoiceController = (InvoiceController)event.getController();
		Invoice invoice = (Invoice)invoiceController.getTo();
		InvoiceDetailController detailController = (InvoiceDetailController)FormUtil.getController(invoiceController.getInvoiceDetailControllerName());
		InvoiceDetail invoiceDetail = (InvoiceDetail)detailController.getTo();
		double amount = (invoiceDetail != null) ? invoiceDetail.getTotalSalesPrice() : 0;
		if (invoiceController.getFinanceGenerationMode() == 1 && amount != 0) {
			Finance finance = (Finance)financeController.getTo();
			if (finance != null) {
				finance.setInvoice(invoice);
				finance.setAmount(amount);
			}
		} else {
			financeController.onCancel(null);
		}

		super.afterBeanAdded(event);
	}

}
