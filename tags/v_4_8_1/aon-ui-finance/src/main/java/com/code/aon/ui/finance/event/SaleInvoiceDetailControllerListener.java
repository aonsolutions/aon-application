package com.code.aon.ui.finance.event;

import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.enumeration.InvoiceSource;
import com.code.aon.ui.finance.controller.SaleInvoiceDetailController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class SaleInvoiceDetailControllerListener extends ControllerAdapter {

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		SaleInvoiceDetailController controller = (SaleInvoiceDetailController)event.getController();
		controller.setLongDescription(false);
	}

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		SaleInvoiceDetailController controller = (SaleInvoiceDetailController)event.getController();
		InvoiceDetail invoiceDetail = (InvoiceDetail)controller.getTo();
		if (invoiceDetail.getDescription().length() > 64) {
			controller.setLongDescription(true);
		} else {
			controller.setLongDescription(false);
		}
	}

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		InvoiceDetail invoiceDetail = (InvoiceDetail)event.getController().getTo();
		invoiceDetail.setSource(InvoiceSource.DIRECT_INVOICE);
		obtainTaxableBase(event, invoiceDetail);
	}

	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		InvoiceDetail invoiceDetail = (InvoiceDetail)event.getController().getTo();
		obtainTaxableBase(event, invoiceDetail);
	}

	private void obtainTaxableBase(ControllerEvent event, InvoiceDetail invoiceDetail) {
		SaleInvoiceDetailController controller = (SaleInvoiceDetailController)event.getController();
		invoiceDetail.setTaxableBase(controller.getPriceStrategy().getBasePrice(invoiceDetail));
	}

}