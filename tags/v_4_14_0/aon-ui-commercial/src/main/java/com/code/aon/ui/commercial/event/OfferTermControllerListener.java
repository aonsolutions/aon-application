package com.code.aon.ui.commercial.event;

import com.code.aon.commercial.CommercialTerm;
import com.code.aon.commercial.OfferTerm;
import com.code.aon.ui.commercial.controller.OfferTermController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class OfferTermControllerListener extends ControllerAdapter {

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		OfferTermController controller = (OfferTermController) event.getController();
		OfferTerm offerTerm = (OfferTerm) controller.getTo();
		offerTerm.setGeneral( controller.isGeneral() );
		controller.setCommercialTerm( new CommercialTerm() );
	}

	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		OfferTermController controller = (OfferTermController) event.getController();
		controller.setCommercialTerm( new CommercialTerm() );
	}

}