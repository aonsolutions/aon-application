package com.code.aon.ui.finance.event;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.enumeration.InvoiceStatus;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.ui.finance.controller.PurchaseInvoiceController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class PurchaseInvoiceControllerListener extends ControllerAdapter {
	
	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		try {
			PurchaseInvoiceController purchaseInvoiceController = (PurchaseInvoiceController)this.getController(); 
			purchaseInvoiceController.loadAddresses(null);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage());
		}
	}

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		try {
			PurchaseInvoiceController purchaseInvoiceController = (PurchaseInvoiceController)this.getController(); 
			Invoice invoice = (Invoice) purchaseInvoiceController.getTo();
			purchaseInvoiceController.loadAddresses(invoice.getRegistry().getId());
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage());
		}
	}

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		Invoice invoice = (Invoice)event.getController().getTo();
		invoice.setType(InvoiceType.PURCHASE);
		invoice.setStatus(InvoiceStatus.PENDING);
	}

}