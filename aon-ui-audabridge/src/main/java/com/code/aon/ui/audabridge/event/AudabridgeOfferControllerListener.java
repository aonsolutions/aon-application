package com.code.aon.ui.audabridge.event;

import com.code.aon.commercial.Offer;
import com.code.aon.commercial.enumeration.OfferType;
import com.code.aon.AonVersion;
import com.code.aon.ui.audabridge.IAudaBridgeConstants;
import com.code.aon.ui.audabridge.controller.AudabridgeOfferController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;

public class AudabridgeOfferControllerListener extends ControllerAdapter {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		Offer offer = (Offer) event.getController().getTo();
		if (offer.getType() == OfferType.AUDATEX) {
			AudabridgeOfferController c = (AudabridgeOfferController) 
					AonUtil.getRegisteredBean(IAudaBridgeConstants.AUDABRIDGE_OFFER_CONTROLLER);
				c.setOffer(offer);
		}
	}

	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		Offer offer = (Offer) event.getController().getTo();
		if (offer.getType() == OfferType.AUDATEX) {
			AudabridgeOfferController c = (AudabridgeOfferController) 
			AonUtil.getRegisteredBean(IAudaBridgeConstants.AUDABRIDGE_OFFER_CONTROLLER);
			c.setOffer(offer);
			c.onCreateAssessment(null);
		}
	}
}
