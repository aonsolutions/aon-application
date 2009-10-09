package com.code.aon.sales.event;

import com.code.aon.commercial.Offer;
import com.code.aon.commercial.OfferDetail;
import com.code.aon.commercial.dao.ICommercialAlias;
import com.code.aon.commercial.enumeration.OfferDetailStatus;
import com.code.aon.commercial.enumeration.OfferStatus;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanListenerAdapter;
import com.code.aon.ql.Criteria;
import com.code.aon.sales.SalesDetail;

public class SalesDetailBeanListener extends ManagerBeanListenerAdapter {

	@Override
	public void beanRemoved(ManagerBeanEvent evt) throws ManagerBeanException {
		SalesDetail salesDetail = (SalesDetail)evt.getTo();
		if (salesDetail.getOfferDetail() != null && salesDetail.getOfferDetail().getId() != null) {
			updateRelatedOffer(salesDetail.getOfferDetail());
		}
	}

	private void updateRelatedOffer(OfferDetail offerDetail) throws ManagerBeanException {
		IManagerBean offerDetailBean = BeanManager.getManagerBean(OfferDetail.class);
		offerDetail.setStatus(OfferDetailStatus.PENDING);
		offerDetailBean.update(offerDetail);

		Criteria criteria = new Criteria();
		criteria.addEqualExpression(offerDetailBean.getFieldName(ICommercialAlias.OFFER_DETAIL_OFFER_ID), offerDetail.getOffer().getId());
		criteria.addEqualExpression(offerDetailBean.getFieldName(ICommercialAlias.OFFER_DETAIL_STATUS), OfferDetailStatus.ON_SALE);
		if (offerDetailBean.getCount(criteria) == 0) {
			IManagerBean offerBean = BeanManager.getManagerBean(Offer.class);
			Offer offer = offerDetail.getOffer();
			offer.setStatus(OfferStatus.PENDING);
			offerBean.update(offer);
		}
	}

}
