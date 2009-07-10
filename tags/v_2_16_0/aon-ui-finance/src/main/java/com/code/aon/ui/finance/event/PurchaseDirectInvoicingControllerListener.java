package com.code.aon.ui.finance.event;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.InvoiceStatus;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.ui.finance.controller.PurchaseDirectInvoicingController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class PurchaseDirectInvoicingControllerListener extends ControllerAdapter {
	
	@Override
	public void beforeModelInitialized(ControllerEvent event) throws ControllerListenerException {
		try {
			event.getController().getCriteria().addEqualExpression(event.getController().getFieldName(IFinanceAlias.INVOICE_TYPE), InvoiceType.PURCHASE);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}
	
	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		((Invoice)event.getController().getTo()).setStatus(InvoiceStatus.PENDING);
		((Invoice)event.getController().getTo()).setType(InvoiceType.PURCHASE);
	}

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		try {
			PurchaseDirectInvoicingController purchaseInvoicingController = (PurchaseDirectInvoicingController)this.getController(); 
			purchaseInvoicingController.loadAddresses(null);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage());
		}
	}
	
	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		try {
			PurchaseDirectInvoicingController purchaseInvoicingController = (PurchaseDirectInvoicingController)this.getController(); 
			Invoice invoice = (Invoice)purchaseInvoicingController.getTo();
			purchaseInvoicingController.loadAddresses(invoice.getRegistry().getId());
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage());
		}
	}

}