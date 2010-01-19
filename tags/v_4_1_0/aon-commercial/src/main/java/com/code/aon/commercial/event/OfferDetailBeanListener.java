package com.code.aon.commercial.event;

import java.util.List;

import com.code.aon.commercial.OfferDetail;
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
public class OfferDetailBeanListener extends ManagerBeanListenerAdapter {

	private boolean updating = false;

	@Override
	public void beanInserted(ManagerBeanEvent event) throws ManagerBeanException {
		OfferDetail detail = (OfferDetail)event.getTo();
		IManagerBean offerDetailBean = BeanManager.getManagerBean(OfferDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(offerDetailBean.getFieldName(ICommercialAlias.OFFER_DETAIL_OFFER_ID), detail.getOffer().getId());
		criteria.addExpression(ExpressionUtilities.getNotEqualExpression(offerDetailBean.getFieldName(ICommercialAlias.OFFER_DETAIL_ID), detail.getId()));
		criteria.addGreaterThanOrEqualExpression(offerDetailBean.getFieldName(ICommercialAlias.OFFER_DETAIL_LINE), detail.getLine());
		criteria.addOrder(offerDetailBean.getFieldName(ICommercialAlias.OFFER_DETAIL_LINE));
		List<ITransferObject> list = offerDetailBean.getList(criteria);
		int index = detail.getLine();
		for (ITransferObject to : list) {
			OfferDetail offerDetail = (OfferDetail)to;
			if (index == offerDetail.getLine()) {
				offerDetail.setLine(index + 1);
				offerDetailBean.update(offerDetail);
				++index;
			}
		}
	}
	
	@Override
	public void beanUpdated(ManagerBeanEvent event) throws ManagerBeanException {
		if (!updating) {
			updating = true;

			OfferDetail detail = (OfferDetail)event.getTo();
			IManagerBean offerDetailBean = BeanManager.getManagerBean(OfferDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(offerDetailBean.getFieldName(ICommercialAlias.OFFER_DETAIL_OFFER_ID), detail.getOffer().getId());
			criteria.addExpression(ExpressionUtilities.getNotEqualExpression(offerDetailBean.getFieldName(ICommercialAlias.OFFER_DETAIL_ID), detail.getId()));
			criteria.addEqualExpression(offerDetailBean.getFieldName(ICommercialAlias.OFFER_DETAIL_LINE), detail.getLine());
			if (offerDetailBean.getCount(criteria) > 0) {
				criteria = new Criteria();
				criteria.addEqualExpression(offerDetailBean.getFieldName(ICommercialAlias.OFFER_DETAIL_OFFER_ID), detail.getOffer().getId());
				criteria.addExpression(ExpressionUtilities.getNotEqualExpression(offerDetailBean.getFieldName(ICommercialAlias.OFFER_DETAIL_ID), detail.getId()));
				criteria.addOrder(offerDetailBean.getFieldName(ICommercialAlias.OFFER_DETAIL_LINE));
				List<ITransferObject> list = offerDetailBean.getList(criteria);
				int index = 1;
				for (ITransferObject to : list) {
					OfferDetail offerDetail = (OfferDetail)to;
					if (index == detail.getLine()) {
						++index;
					}
					offerDetail.setLine(index);
					offerDetailBean.update(offerDetail);
					++index;
				}
			}

			updating = false;
		}
	}

	@Override
	public void beanRemoved(ManagerBeanEvent event) throws ManagerBeanException {
		OfferDetail detail = (OfferDetail)event.getTo();
		IManagerBean offerDetailBean = BeanManager.getManagerBean(OfferDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(offerDetailBean.getFieldName(ICommercialAlias.OFFER_DETAIL_OFFER_ID), detail.getOffer().getId());
		criteria.addExpression(ExpressionUtilities.getNotEqualExpression(offerDetailBean.getFieldName(ICommercialAlias.OFFER_DETAIL_ID), detail.getId()));
		criteria.addGreaterThanOrEqualExpression(offerDetailBean.getFieldName(ICommercialAlias.OFFER_DETAIL_LINE), detail.getLine());
		criteria.addOrder(offerDetailBean.getFieldName(ICommercialAlias.OFFER_DETAIL_LINE));
		List<ITransferObject> list = offerDetailBean.getList(criteria);
		int index = detail.getLine() + 1;
		for (ITransferObject to : list) {
			OfferDetail offerDetail = (OfferDetail)to;
			if (index == offerDetail.getLine()) {
				offerDetail.setLine(index - 1);
				offerDetailBean.update(offerDetail);
				++ index;
			}
		}
	}

}
