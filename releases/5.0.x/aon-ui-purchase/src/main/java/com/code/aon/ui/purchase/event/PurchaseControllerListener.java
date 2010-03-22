package com.code.aon.ui.purchase.event;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.purchase.Purchase;
import com.code.aon.purchase.enumeration.PurchaseDocumentType;
import com.code.aon.purchase.enumeration.PurchaseStatus;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.purchase.controller.PurchaseController;

/**
 * Listener Added to the OfferController.
 * 
 * @author Consulting & Development. Joseba Urkiri - 6-sept-2006
 * @since 1.0
 */
public class PurchaseControllerListener extends ControllerAdapter {

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		PurchaseController controller = (PurchaseController)event.getController();
		((Purchase)controller.getTo()).setStatus(PurchaseStatus.PENDING);
		((Purchase)controller.getTo()).setDocumentType(PurchaseDocumentType.NORMAL);
		controller.setAddresses(null);
		controller.setDefaultPayMethod(null);
		controller.resetPurchasePayMethod();
	}

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		PurchaseController controller = (PurchaseController)event.getController();
		try {
			controller.loadAddresses(((Purchase)controller.getTo()).getSupplier().getRegistry().getId());
			controller.loadDefaultPayMethod(((Purchase)controller.getTo()).getSupplier().getRegistry().getId(), true);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage());
		}
	}

}