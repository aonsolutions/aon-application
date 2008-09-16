package com.code.aon.ui.commercial.event;

import com.code.aon.commercial.Offer;
import com.code.aon.commercial.enumeration.OfferStatus;
import com.code.aon.ui.commercial.controller.OfferController;
import com.code.aon.ui.commercial.controller.OfferDetailController;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.form.listener.LinesControllerListener;
import com.code.aon.ui.util.AonUtil;

/**
 * Listener Added to the OfferController.
 * 
 * @author Consulting & Development. Joseba Urkiri - 6-sept-2006
 * @since 1.0
 */
public class OfferControllerListener extends LinesControllerListener {
	
	/** OfferDetail Controller name. */
	private static final String OFFER_DETAIL_CONTROLLER_NAME = "offerDetail";

	/**
	 * After bean canceled. Cancels the offerDetailController 
	 * 
	 * @param event the event
	 * 
	 * @throws ControllerListenerException the controller listener exception
	 */
	@Override
	public void afterBeanCanceled(ControllerEvent event) throws ControllerListenerException {
		OfferDetailController offerDetailController = (OfferDetailController)AonUtil.getController(OFFER_DETAIL_CONTROLLER_NAME);
		offerDetailController.setNew(false);
	}
	
	/**
	 * Before bean added. Sets to null <code>seller</code> and <code>payMethod</code> fields
	 * to avoid TransientObjectException
	 * 
	 * @param event the event
	 * 
	 * @throws ControllerListenerException the controller listener exception
	 */
	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		((Offer)((OfferController)event.getController()).getTo()).setStatus(OfferStatus.PENDING);
		((Offer)((OfferController)event.getController()).getTo()).setSeller(null);
		((Offer)((OfferController)event.getController()).getTo()).setPayMethod(null);
		super.beforeBeanAdded(event);
	}
	
	/**
	 * Before bean created. Set to null the supportOrderId of the controller
	 * 
	 * @param event the event
	 * 
	 * @throws ControllerListenerException the controller listener exception
	 */
	@Override
	public void beforeBeanCreated(ControllerEvent event) throws ControllerListenerException {
		OfferController offerController = (OfferController)event.getController();
        offerController.setSupportOrderId(null);
        offerController.setDescription(null);
	}

	/**
	 * Before bean updated. Sets to null <code>seller</code> and <code>payMethod</code> fields
	 * to avoid TransientObjectException
	 * 
	 * @param event the event
	 * 
	 * @throws ControllerListenerException the controller listener exception
	 */
	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		((Offer)((OfferController)event.getController()).getTo()).setSeller(null);
		((Offer)((OfferController)event.getController()).getTo()).setPayMethod(null);
		super.beforeBeanUpdated(event);
	}
}