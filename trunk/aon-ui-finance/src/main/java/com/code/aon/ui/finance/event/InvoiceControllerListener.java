package com.code.aon.ui.finance.event;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.Invoice;
import com.code.aon.ui.finance.controller.SaleInvoiceController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class InvoiceControllerListener extends ControllerAdapter {

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		try {
			SaleInvoiceController saleInvoiceController = (SaleInvoiceController)this.getController(); 
			saleInvoiceController.loadAddresses(null);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage());
		}
	}

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		try {
			SaleInvoiceController saleInvoiceController = (SaleInvoiceController)this.getController(); 
			Invoice invoice = (Invoice) saleInvoiceController.getTo();
			saleInvoiceController.loadAddresses(invoice.getRegistry().getId());
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage());
		}
	}

}
