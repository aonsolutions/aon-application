package com.code.aon.ui.finance.event;

import com.code.aon.AonVersion;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.ui.finance.controller.SaleInvoiceController;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class SaleInvoiceControllerListener extends InvoiceControllerListener {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	@Override
	public void afterBeanReset(ControllerEvent event) throws ControllerListenerException {
		super.afterBeanReset(event);
		initializeCommunicationStatus( event);
	}
	
	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		Invoice invoice = (Invoice)event.getController().getTo();
		invoice.setType(InvoiceType.SALES);
		super.afterBeanCreated(event);
		initializeCommunicationStatus( event);
	}

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		Invoice invoice = (Invoice)event.getController().getTo();
		invoice.setType(InvoiceType.SALES);
	}

	@Override
	public void afterModelInitialized(ControllerEvent event) throws ControllerListenerException {
		initializeCommunicationStatus( event);
		super.afterModelInitialized(event);
	}
	
	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		initializeCommunicationStatus( event);
		super.afterBeanSelected(event);
	}
	
	private void initializeCommunicationStatus(ControllerEvent event) {
		SaleInvoiceController controller = (SaleInvoiceController) event.getController();
		controller.setCommunicationStatus(null);
	}
	

}