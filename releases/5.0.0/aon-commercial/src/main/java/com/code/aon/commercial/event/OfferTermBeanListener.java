package com.code.aon.commercial.event;

import java.util.List;

import com.code.aon.commercial.OfferTerm;
import com.code.aon.commercial.dao.ICommercialAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanListenerAdapter;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionUtilities;

/**
 * @author Consulting & Development
 * 
 */
public class OfferTermBeanListener extends ManagerBeanListenerAdapter {

	private boolean updating = false;

	@Override
	public void beanInserted(ManagerBeanEvent event) throws ManagerBeanException {
		OfferTerm offerTerm = (OfferTerm)event.getTo();
		IManagerBean offerTermBean = BeanManager.getManagerBean(OfferTerm.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(offerTermBean.getFieldName(ICommercialAlias.OFFER_TERM_OFFER_ID), offerTerm.getOffer().getId());
		criteria.addExpression(ExpressionUtilities.getNotEqualExpression(offerTermBean.getFieldName(ICommercialAlias.OFFER_TERM_ID), offerTerm.getId()));
		criteria.addGreaterThanOrEqualExpression(offerTermBean.getFieldName(ICommercialAlias.OFFER_TERM_LINE), offerTerm.getLine());
		criteria.addOrder(offerTermBean.getFieldName(ICommercialAlias.OFFER_TERM_LINE));
		List<ITransferObject> list = offerTermBean.getList(criteria);
		int index = offerTerm.getLine();
		for (ITransferObject to : list) {
			OfferTerm term = (OfferTerm)to;
			if (index == term.getLine()) {
				term.setLine(index + 1);
				offerTermBean.update(term);
				++index;
			}
		}
	}
	
	@Override
	public void beanUpdated(ManagerBeanEvent event) throws ManagerBeanException {
		if (!updating) {
			updating = true;

			OfferTerm offerTerm = (OfferTerm)event.getTo();
			IManagerBean offerTermBean = BeanManager.getManagerBean(OfferTerm.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(offerTermBean.getFieldName(ICommercialAlias.OFFER_TERM_OFFER_ID), offerTerm.getOffer().getId());
			criteria.addExpression(ExpressionUtilities.getNotEqualExpression(offerTermBean.getFieldName(ICommercialAlias.OFFER_TERM_ID), offerTerm.getId()));
			criteria.addEqualExpression(offerTermBean.getFieldName(ICommercialAlias.OFFER_TERM_LINE), offerTerm.getLine());
			if (offerTermBean.getCount(criteria) > 0) {
				criteria = new Criteria();
				criteria.addEqualExpression(offerTermBean.getFieldName(ICommercialAlias.OFFER_TERM_OFFER_ID), offerTerm.getOffer().getId());
				criteria.addExpression(ExpressionUtilities.getNotEqualExpression(offerTermBean.getFieldName(ICommercialAlias.OFFER_TERM_ID), offerTerm.getId()));
				criteria.addOrder(offerTermBean.getFieldName(ICommercialAlias.OFFER_TERM_LINE));
				List<ITransferObject> list = offerTermBean.getList(criteria);
				int index = 1;
				for (ITransferObject to : list) {
					OfferTerm term = (OfferTerm)to;
					if (index == term.getLine()) {
						++index;
					}
					term.setLine(index);
					offerTermBean.update(term);
					++index;
				}
			}

			updating = false;
		}
	}

	@Override
	public void beanRemoved(ManagerBeanEvent event) throws ManagerBeanException {
		OfferTerm offerTerm = (OfferTerm)event.getTo();
		IManagerBean offerTermBean = BeanManager.getManagerBean(OfferTerm.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(offerTermBean.getFieldName(ICommercialAlias.OFFER_TERM_OFFER_ID), offerTerm.getOffer().getId());
		criteria.addExpression(ExpressionUtilities.getNotEqualExpression(offerTermBean.getFieldName(ICommercialAlias.OFFER_TERM_ID), offerTerm.getId()));
		criteria.addGreaterThanOrEqualExpression(offerTermBean.getFieldName(ICommercialAlias.OFFER_TERM_LINE), offerTerm.getLine());
		criteria.addOrder(offerTermBean.getFieldName(ICommercialAlias.OFFER_TERM_LINE));
		List<ITransferObject> list = offerTermBean.getList(criteria);
		int index = offerTerm.getLine() + 1;
		for (ITransferObject to : list) {
			OfferTerm term = (OfferTerm)to;
			if (index == term.getLine()) {
				term.setLine(index - 1);
				offerTermBean.update(offerTerm);
				++ index;
			}
		}
	}

}
