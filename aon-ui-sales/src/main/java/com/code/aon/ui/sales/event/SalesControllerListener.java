package com.code.aon.ui.sales.event;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.sales.Sales;
import com.code.aon.sales.enumeration.DocumentType;
import com.code.aon.sales.enumeration.SalesStatus;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.sales.controller.ISalesConstants;
import com.code.aon.ui.sales.controller.SalesController;

public class SalesControllerListener extends ControllerAdapter implements ISalesConstants {

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		SalesController controller = (SalesController)event.getController();
		((Sales)controller.getTo()).setSecurityLevel(SecurityLevel.OFFICIAL);
		((Sales)controller.getTo()).setStatus(SalesStatus.PENDING);
		((Sales)controller.getTo()).setDocumentType(DocumentType.NORMAL);
		controller.setAddresses(null);
		controller.setProjects(null);
		controller.setDefaultPayMethod(null);
		controller.resetSalesPayMethod();
	}

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		SalesController controller = (SalesController)event.getController();
		try {
			controller.loadAddresses(((Sales)controller.getTo()).getCustomer().getRegistry().getId());
			controller.loadProjects(((Sales)controller.getTo()).getCustomer().getRegistry().getId());
			controller.loadDefaultPayMethod(((Sales)controller.getTo()).getCustomer().getRegistry().getId(), true);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage());
		}
	}

	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		IController salesDetailController = FormUtil.getController(SALES_DETAIL_CONTROLLER_NAME);
		salesDetailController.onReset(null);
	}
	
}