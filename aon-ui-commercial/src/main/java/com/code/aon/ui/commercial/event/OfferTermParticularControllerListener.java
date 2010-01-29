package com.code.aon.ui.commercial.event;

import com.code.aon.commercial.OfferTerm;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class OfferTermParticularControllerListener extends ControllerAdapter {

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		OfferTerm offerTerm= (OfferTerm) event.getController().getTo();
		offerTerm.setParticular(true);
	}

}