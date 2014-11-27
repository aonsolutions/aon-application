package com.code.aon.ui.finance.event;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.enumeration.InvoiceStatus;
import com.code.aon.finance.enumeration.RectificationType;
import com.code.aon.ui.finance.controller.InvoiceController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class InvoiceControllerListener extends ControllerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
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

			invoiceController.initSeries();
			invoiceController.loadAddresses(null);
			invoiceController.setProjects(null);
			invoiceController.setSavedProject(null);
			invoiceController.setShowProjectLookup(false);
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

			invoiceController.initSeries(false);
			invoiceController.loadAddresses(invoice.getRegistry().getId());
			invoiceController.setProjects(null);
			invoiceController.setSavedProject(invoice.getProject());
			invoiceController.setShowProjectLookup(true);
			invoiceController.setFinanceGenerationMode(0);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage());
		}
	}
	
	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		InvoiceController invoiceController = (InvoiceController)this.getController();
		Invoice invoice = (Invoice)invoiceController.getTo();

		invoiceController.setSavedProject(invoice.getProject());
		invoiceController.setShowProjectLookup(true);
	}
	
	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		InvoiceController invoiceController = (InvoiceController)this.getController();
		try {
			invoiceController.linkProject(invoiceController.getInvoice(), true);
			invoiceController.autoGenerateIncreases();
			invoiceController.autoGenerateFinances();
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage());
		}
	}
	
}
