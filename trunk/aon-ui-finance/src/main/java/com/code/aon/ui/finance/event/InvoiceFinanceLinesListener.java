package com.code.aon.ui.finance.event;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.Finance;
import com.code.aon.finance.Invoice;
import com.code.aon.ui.finance.controller.InvoiceController;
import com.code.aon.ui.finance.controller.InvoiceFinanceController;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.form.listener.LinesControllerListener;

public class InvoiceFinanceLinesListener extends LinesControllerListener {
	
	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		InvoiceFinanceController financeController = (InvoiceFinanceController)getLinesController();
		InvoiceController invoiceController = (InvoiceController)event.getController();
		Invoice invoice = (Invoice)invoiceController.getTo();
		if (invoiceController.getFinanceGenerationMode() == 1 && invoice.getTotal() != 0) {
			Finance finance = (Finance)financeController.getTo();
			if (finance != null) {
				finance.setInvoice(invoice);
				finance.setAmount(invoice.getTotal());
			}
		} else {
			financeController.onCancel(null);
		}

		if (invoiceController.getFinanceGenerationMode() == 0 && invoice.getTotal() != 0) {
			try {
				invoiceController.getFinanceGenerator().generateFinances(invoice, invoice.getTotal());
			} catch (ManagerBeanException ex) {
				throw new ControllerListenerException(ex.getMessage(), ex);
			}
		}

		super.afterBeanAdded(event);
	}

}
