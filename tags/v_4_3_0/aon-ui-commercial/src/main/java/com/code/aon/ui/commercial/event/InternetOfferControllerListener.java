package com.code.aon.ui.commercial.event;

import com.code.aon.commercial.Offer;
import com.code.aon.commercial.dao.ICommercialAlias;
import com.code.aon.commercial.enumeration.OfferType;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.commercial.controller.OfferController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

/**
 * Listener Added to the OfferController.
 * 
 * @author Esferalia. David Uriarte - 14-sept-2009
 * @since 1.0
 */
public class InternetOfferControllerListener extends ControllerAdapter {

	@Override
	public void beforeModelInitialized(ControllerEvent event) throws ControllerListenerException {
		OfferController controller = (OfferController)event.getController();
		try {
			controller.getCriteria().addEqualExpression(controller.getFieldName(ICommercialAlias.OFFER_TYPE), OfferType.INTERNET);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}		
	}

	@Override
	public void afterBeanCreated(ControllerEvent event)	throws ControllerListenerException {
		OfferController controller = (OfferController)event.getController();
		((Offer)controller.getTo()).setType(OfferType.INTERNET);
	}

}