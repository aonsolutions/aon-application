package com.code.aon.ui.commercial.event;

import com.code.aon.commercial.Offer;
import com.code.aon.commercial.OfferDetail;
import com.code.aon.commercial.dao.ICommercialAlias;
import com.code.aon.commercial.enumeration.OfferDetailStatus;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ui.commercial.controller.OfferDetailController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class OfferDetailControllerListener extends ControllerAdapter {

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		OfferDetailController controller = (OfferDetailController)event.getController();
		OfferDetail offerDetail = (OfferDetail)controller.getTo();

		controller.setLongDescription(false);
		try {
			offerDetail.setLine(calculateNextLine((Offer)controller.getMasterController().getTo()));
			offerDetail.setStatus(OfferDetailStatus.PENDING);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		event.getController().initializeModel();
	}

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		OfferDetailController controller = (OfferDetailController)event.getController();
		OfferDetail offerDetail = (OfferDetail)controller.getTo();

		controller.setLongDescription((offerDetail.getDescription().length() > 64) ? true : false);
	}

	private	Integer calculateNextLine(Offer offer) throws ManagerBeanException {
		IManagerBean offerDetailBean = BeanManager.getManagerBean(OfferDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(offerDetailBean.getFieldName(ICommercialAlias.OFFER_DETAIL_OFFER_ID), offer.getId());
		Projection projection = Projection.max(offerDetailBean.getFieldName(ICommercialAlias.OFFER_DETAIL_LINE));
		Object value = offerDetailBean.getUniqueResult(projection, criteria);
		return (value != null) ? ((Integer)value) + 1 : 1;
	}

}