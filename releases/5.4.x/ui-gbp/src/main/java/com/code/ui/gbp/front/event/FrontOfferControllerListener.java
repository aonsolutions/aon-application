package com.code.ui.gbp.front.event;

import java.util.Date;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.gbp.Offer;
import com.code.gbp.Supplier;
import com.code.gbp.dao.IGBPAlias;
import com.code.gbp.enumeration.OfferStatus;
import com.code.ui.gbp.constants.GBPConstants;
import com.code.ui.gbp.front.util.FrontUtil;

public class FrontOfferControllerListener extends ControllerAdapter {

	@Override
	public void beforeModelInitialized(ControllerEvent event) throws ControllerListenerException {
		try {
			Supplier currentSupplier = FrontUtil.getCurrentSupplier();
			event.getController().getCriteria().addEqualExpression(event.getController().getFieldName(IGBPAlias.OFFER_SUPPLIER_ID), currentSupplier.getId());
			event.getController().getCriteria().addOrder(event.getController().getFieldName(IGBPAlias.OFFER_CAMPAIGN_CODE));
			event.getController().getCriteria().addOrder(event.getController().getFieldName(IGBPAlias.OFFER_OFFER_DATE), false);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}
	
	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		((Offer)event.getController().getTo()).setSupplier(FrontUtil.getCurrentSupplier());
		((Offer)event.getController().getTo()).setOfferDate(new Date());
		((Offer)event.getController().getTo()).setStatus(OfferStatus.PENDING);
		((Offer)event.getController().getTo()).setPaymentTerm(GBPConstants.GBP_DEFAULT_PAYMENT_TERM);
	}
}