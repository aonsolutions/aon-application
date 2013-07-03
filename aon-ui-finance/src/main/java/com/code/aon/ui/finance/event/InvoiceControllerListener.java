package com.code.aon.ui.finance.event;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.enumeration.InvoiceStatus;
import com.code.aon.finance.enumeration.RectificationType;
import com.code.aon.ui.finance.controller.InvoiceController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class InvoiceControllerListener extends ControllerAdapter {
	
	@Override
	public void afterModelInitialized(ControllerEvent event)throws ControllerListenerException {
		InvoiceController controller = (InvoiceController)event.getController();
		controller.setTotalInvoiceAmount(null);
	}

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		try {
			InvoiceController invoiceController = (InvoiceController)this.getController(); 
			Invoice invoice = (Invoice)invoiceController.getTo();
			invoice.setStatus(InvoiceStatus.PENDING);
			invoice.setRectificationType(RectificationType.NONE);
			invoice.setSecurityLevel(SecurityLevel.OFFICIAL);
			invoice.setTaxDate(invoice.getIssueDate());

			invoiceController.loadAddresses(null);
			invoiceController.loadProjects(null);
			invoiceController.setFinanceGenerationMode(0);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage());
		}
	}

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		try {
			InvoiceController invoiceController = (InvoiceController)this.getController(); 
			Invoice invoice = (Invoice)invoiceController.getTo();

			invoiceController.loadAddresses(invoice.getRegistry().getId());
			invoiceController.loadProjects(invoice.getRegistry().getId());
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage());
		}
	}
	
	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		InvoiceController invoiceController = (InvoiceController)this.getController();
		Invoice invoice = (Invoice)invoiceController.getTo();
		if (invoice.getProject() != null && invoice.getProject().getId() != null) {
			IController invoiceDetailController = FormUtil.getController(invoiceController.getInvoiceDetailControllerName());
			invoiceDetailController.onSearch(null);
		}
	}
	
}
