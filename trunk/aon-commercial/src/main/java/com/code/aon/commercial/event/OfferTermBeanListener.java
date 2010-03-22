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
		OfferTerm term = (OfferTerm)event.getTo();
		IManagerBean offerTermBean = BeanManager.getManagerBean(OfferTerm.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(offerTermBean.getFieldName(ICommercialAlias.OFFER_TERM_OFFER_ID), term.getOffer().getId());
		criteria.addExpression(ExpressionUtilities.getNotEqualExpression(offerTermBean.getFieldName(ICommercialAlias.OFFER_TERM_ID), term.getId()));
		criteria.addGreaterThanOrEqualExpression(offerTermBean.getFieldName(ICommercialAlias.OFFER_TERM_LINE), term.getLine());
		criteria.addOrder(offerTermBean.getFieldName(ICommercialAlias.OFFER_TERM_LINE));
		List<ITransferObject> list = offerTermBean.getList(criteria);
		int index = term.getLine();
		for (ITransferObject to : list) {
			OfferTerm offerTerm = (OfferTerm)to;
			if (index == offerTerm.getLine()) {
				offerTerm.setLine(index + 1);
				offerTermBean.update(offerTerm);
				++index;
			}
		}
	}
	
	@Override
	public void beanUpdated(ManagerBeanEvent event) throws ManagerBeanException {
		if (!updating) {
			updating = true;

			OfferTerm term = (OfferTerm)event.getTo();
			IManagerBean offerTermBean = BeanManager.getManagerBean(OfferTerm.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(offerTermBean.getFieldName(ICommercialAlias.OFFER_TERM_OFFER_ID), term.getOffer().getId());
			criteria.addExpression(ExpressionUtilities.getNotEqualExpression(offerTermBean.getFieldName(ICommercialAlias.OFFER_TERM_ID), term.getId()));
			criteria.addEqualExpression(offerTermBean.getFieldName(ICommercialAlias.OFFER_TERM_LINE), term.getLine());
			if (offerTermBean.getCount(criteria) > 0) {
				criteria = new Criteria();
				criteria.addEqualExpression(offerTermBean.getFieldName(ICommercialAlias.OFFER_TERM_OFFER_ID), term.getOffer().getId());
				criteria.addExpression(ExpressionUtilities.getNotEqualExpression(offerTermBean.getFieldName(ICommercialAlias.OFFER_TERM_ID), term.getId()));
				criteria.addOrder(offerTermBean.getFieldName(ICommercialAlias.OFFER_TERM_LINE));
				List<ITransferObject> list = offerTermBean.getList(criteria);
				int index = 1;
				for (ITransferObject to : list) {
					OfferTerm offerTerm = (OfferTerm)to;
					if (index == term.getLine()) {
						++index;
					}
					offerTerm.setLine(index);
					offerTermBean.update(offerTerm);
					++index;
				}
			}

			updating = false;
		}
	}

	@Override
	public void beanRemoved(ManagerBeanEvent event) throws ManagerBeanException {
		OfferTerm term = (OfferTerm)event.getTo();
		IManagerBean offerTermBean = BeanManager.getManagerBean(OfferTerm.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(offerTermBean.getFieldName(ICommercialAlias.OFFER_TERM_OFFER_ID), term.getOffer().getId());
		criteria.addExpression(ExpressionUtilities.getNotEqualExpression(offerTermBean.getFieldName(ICommercialAlias.OFFER_TERM_ID), term.getId()));
		criteria.addGreaterThanOrEqualExpression(offerTermBean.getFieldName(ICommercialAlias.OFFER_TERM_LINE), term.getLine());
		criteria.addOrder(offerTermBean.getFieldName(ICommercialAlias.OFFER_TERM_LINE));
		List<ITransferObject> list = offerTermBean.getList(criteria);
		int index = term.getLine() + 1;
		for (ITransferObject to : list) {
			OfferTerm offerTerm = (OfferTerm)to;
			if (index == offerTerm.getLine()) {
				offerTerm.setLine(index - 1);
				offerTermBean.update(offerTerm);
				++ index;
			}
		}
	}

}
