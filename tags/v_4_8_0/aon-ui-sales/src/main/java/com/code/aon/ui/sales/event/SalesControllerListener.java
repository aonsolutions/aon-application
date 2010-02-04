package com.code.aon.ui.sales.event;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.sales.Sales;
import com.code.aon.sales.enumeration.DocumentType;
import com.code.aon.sales.enumeration.SalesStatus;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.sales.controller.SalesController;

/**
 * Listener Added to the OfferController.
 * 
 * @author Consulting & Development. Joseba Urkiri - 6-sept-2006
 * @since 1.0
 */
public class SalesControllerListener extends ControllerAdapter {

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		SalesController controller = (SalesController)event.getController();
		((Sales)controller.getTo()).setStatus(SalesStatus.PENDING);
		((Sales)controller.getTo()).setDocumentType(DocumentType.NORMAL);
		controller.setAddresses(null);
		controller.setDefaultPayMethod(null);
		controller.resetSalesPayMethod();
	}

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		SalesController controller = (SalesController)event.getController();
		try {
			controller.loadAddresses(((Sales)controller.getTo()).getCustomer().getRegistry().getId());
			controller.loadDefaultPayMethod(((Sales)controller.getTo()).getCustomer().getRegistry().getId(), true);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage());
		}
	}

}