package com.code.aon.ui.commercial.event;

import com.code.aon.commercial.CommercialTerm;
import com.code.aon.commercial.Offer;
import com.code.aon.commercial.OfferTerm;
import com.code.aon.commercial.dao.ICommercialAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
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
		try {
			offerTerm.setLine(calculateNextLine((Offer)controller.getMasterController().getTo(), controller.isGeneral()));
			
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
		controller.setCommercialTerm( new CommercialTerm() );
	}

	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		OfferTermController controller = (OfferTermController) event.getController();
		controller.setCommercialTerm( new CommercialTerm() );
	}
	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		event.getController().initializeModel();
	}

	@Override
	public void afterBeanCanceled(ControllerEvent event) throws ControllerListenerException {
		if (!event.getController().isNew()) {
			event.getController().initializeModel();
		}
	}
	
	private	Integer calculateNextLine(Offer offer, boolean general) throws ManagerBeanException {
		IManagerBean offerTermBean = BeanManager.getManagerBean(OfferTerm.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(offerTermBean.getFieldName(ICommercialAlias.OFFER_TERM_OFFER_ID),offer.getId());
		criteria.addEqualExpression(offerTermBean.getFieldName(ICommercialAlias.OFFER_TERM_GENERAL), general);
		Projection projection = Projection.max(offerTermBean.getFieldName(ICommercialAlias.OFFER_TERM_LINE));
		Object value = offerTermBean.getUniqueResult(projection, criteria);
		return (value != null) ? ((Integer)value) + 1 : 1;
	}

}