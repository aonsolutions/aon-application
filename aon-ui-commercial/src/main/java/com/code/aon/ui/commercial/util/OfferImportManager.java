package com.code.aon.ui.commercial.util;

import java.util.Date;
import java.util.Iterator;

import org.apache.commons.lang.SerializationUtils;

import com.code.aon.commercial.Offer;
import com.code.aon.commercial.OfferAttachment;
import com.code.aon.commercial.OfferDetail;
import com.code.aon.commercial.OfferTerm;
import com.code.aon.commercial.Target;
import com.code.aon.commercial.dao.ICommercialAlias;
import com.code.aon.commercial.enumeration.OfferDetailStatus;
import com.code.aon.commercial.enumeration.OfferStatus;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;

public class OfferImportManager {

	public Offer createOfferVersion(Offer source) throws ManagerBeanException {
		IManagerBean offerBean = BeanManager.getManagerBean(Offer.class);
		Offer offer = (Offer)SerializationUtils.clone(source);
		offer.setId(null);
		offer.setVersion(getNextVersion(source.getSeries(), source.getNumber()));
		offer.setStatus(OfferStatus.PENDING);
		offer.setLines(null);
		offer.setAttachments(null);
		offer.setTerms(null);
		offerBean.insert(offer);

		importOffer(source, offer);
		return offer;
	}

	private int getNextVersion(String series, int number) throws ManagerBeanException {
		int version = 0;
		IManagerBean offerBean = BeanManager.getManagerBean(Offer.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(offerBean.getFieldName(ICommercialAlias.OFFER_SERIES), series);
		criteria.addEqualExpression(offerBean.getFieldName(ICommercialAlias.OFFER_NUMBER), number);
		criteria.addOrder(offerBean.getFieldName(ICommercialAlias.OFFER_VERSION), false);
		Iterator<?> iterator = offerBean.getList(criteria).iterator();
		if (iterator.hasNext()) {
			Offer offer = (Offer)iterator.next();
			version = offer.getVersion();
		}
		return ++version;
	}

	public Offer copyOffer(Offer source, String series, int number, Target target, Date date) throws ManagerBeanException {
		IManagerBean offerBean = BeanManager.getManagerBean(Offer.class);
		Offer offer = (Offer)SerializationUtils.clone(source);
		offer.setId(null);
		offer.setSeries(series);
		offer.setNumber(number);
		offer.setVersion(0);
		offer.setTarget(target);
		offer.setAddress(target.getRegistry().getDefaultAddress());
		offer.setIssueDate(date);
		offer.setStatus(OfferStatus.PENDING);
		offer.setLines(null);
		offer.setAttachments(null);
		offer.setTerms(null);
		offerBean.insert(offer);

		importOffer(source, offer);
		return offer;
	}

	private void importOffer(Offer source, Offer offer) throws ManagerBeanException {
		IManagerBean offerDetailBean = BeanManager.getManagerBean(OfferDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(offerDetailBean.getFieldName(ICommercialAlias.OFFER_DETAIL_OFFER_ID), source.getId());
		for (ITransferObject to : offerDetailBean.getList(criteria)) {
			OfferDetail sourceDetail = (OfferDetail)to;
			OfferDetail offerDetail = (OfferDetail)SerializationUtils.clone(sourceDetail);
			offerDetail.setId(null);
			offerDetail.setOffer(offer);
			offerDetail.setStatus(OfferDetailStatus.PENDING);
			offerDetailBean.insert(offerDetail);
		}

		IManagerBean offerAttachBean = BeanManager.getManagerBean(OfferAttachment.class);
		criteria = new Criteria();
		criteria.addEqualExpression(offerAttachBean.getFieldName(ICommercialAlias.OFFER_ATTACHMENT_OFFER_ID), source.getId());
		for (ITransferObject to : offerAttachBean.getList(criteria)) {
			OfferAttachment sourceAttach = (OfferAttachment)to;
			OfferAttachment offerAttach = (OfferAttachment)SerializationUtils.clone(sourceAttach);
			offerAttach.setId(null);
			offerAttach.setOffer(offer);
			offerAttachBean.insert(offerAttach);
		}

		IManagerBean offerTermBean = BeanManager.getManagerBean(OfferTerm.class);
		criteria = new Criteria();
		criteria.addEqualExpression(offerTermBean.getFieldName(ICommercialAlias.OFFER_TERM_OFFER_ID), source.getId());
		for (ITransferObject to : offerTermBean.getList(criteria)) {
			OfferTerm sourceTerm = (OfferTerm)to;
			OfferTerm offerTerm = (OfferTerm)SerializationUtils.clone(sourceTerm);
			offerTerm.setId(null);
			offerTerm.setOffer(offer);
			offerTermBean.insert(offerTerm);
		}
	}

}
