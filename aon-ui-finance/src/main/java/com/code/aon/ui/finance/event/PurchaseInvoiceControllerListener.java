package com.code.aon.ui.finance.event;

import com.code.aon.finance.Invoice;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class PurchaseInvoiceControllerListener extends InvoiceControllerListener {
	
	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		Invoice invoice = (Invoice)event.getController().getTo();
		invoice.setType(InvoiceType.PURCHASE);

		super.afterBeanCreated(event);
	}

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		Invoice invoice = (Invoice)event.getController().getTo();
		invoice.setType(InvoiceType.PURCHASE);
	}

}