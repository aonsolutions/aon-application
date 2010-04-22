package com.code.aon.ui.commercial.event;

import com.code.aon.commercial.Offer;
import com.code.aon.commercial.enumeration.OfferStatus;
import com.code.aon.commercial.enumeration.OfferType;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.ui.commercial.controller.OfferController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

/**
 * Listener Added to the OfferController.
 * 
 * @author Consulting & Development. Joseba Urkiri - 6-sept-2006
 * @since 1.0
 */
public class OfferControllerListener extends ControllerAdapter {

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		OfferController controller = (OfferController)event.getController();
		((Offer)controller.getTo()).setSecurityLevel(SecurityLevel.OFFICIAL);
		((Offer)controller.getTo()).setStatus(OfferStatus.PENDING);
		((Offer)controller.getTo()).setType(OfferType.NORMAL);
		controller.setAddresses(null);
		controller.setDefaultPayMethod(null);
		controller.resetOfferPayMethod();
	}

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		OfferController controller = (OfferController)event.getController();
		try {
			controller.loadAddresses(((Offer)controller.getTo()).getTarget().getRegistry().getId());
			controller.loadDefaultPayMethod(((Offer)controller.getTo()).getTarget().getRegistry().getId(), true);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage());
		}
	}

}