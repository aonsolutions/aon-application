package com.code.ui.gbp.event;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.code.gbp.Incidence;
import com.code.gbp.Offer;
import com.code.gbp.dao.IGBPAlias;
import com.code.gbp.enumeration.IncidenceSource;
import com.code.gbp.enumeration.OfferStatus;
import com.code.ui.gbp.controller.OfferController;

public class OfferIncidenceControllerListener extends ControllerAdapter {

	private static final String OFFER_CONTROLLER_NAME = "offer";
	
	@Override
	public void beforeModelInitialized(ControllerEvent event) throws ControllerListenerException {
		try {
			event.getController().getCriteria().addEqualExpression(event.getController().getFieldName(IGBPAlias.INCIDENCE_SOURCE), IncidenceSource.OFFER);
			event.getController().getCriteria().addOrder(event.getController().getFieldName(IGBPAlias.INCIDENCE_INCIDENCE_DATE), false);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		try {
			Incidence incidence = (Incidence)event.getController().getTo();
			incidence.setSource(IncidenceSource.OFFER);
			OfferController offerController = (OfferController)AonUtil.getController(OFFER_CONTROLLER_NAME);
			Offer offer = (Offer)offerController.getTo(); 
			updateOfferStatus(offerController, OfferStatus.INCIDENCES);
			incidence.setCampaign(offer.getCampaign());
			incidence.setSupplier(offer.getSupplier());
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}
	
	@Override
	public void beforeBeanRemoved(ControllerEvent event) throws ControllerListenerException {
		try {
			if(event.getController().getModel().getRowCount() == 1){
				OfferController offerController = (OfferController)AonUtil.getController(OFFER_CONTROLLER_NAME);
				updateOfferStatus(offerController, OfferStatus.PENDING);
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}

	private void updateOfferStatus(OfferController offerController, OfferStatus status) throws ManagerBeanException {
		((Offer)offerController.getTo()).setStatus(status);
		offerController.accept(null);
	}
}