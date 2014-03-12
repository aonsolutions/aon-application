package com.code.aon.commercial.event;

import java.util.List;

import com.code.aon.commercial.OfferDetail;
import com.code.aon.common.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanListenerAdapter;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;

/**
 * @author Consulting & Development
 * 
 */
public class OfferDetailBeanListener extends ManagerBeanListenerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private boolean updating = false;

	@Override
	public void beanInserted(ManagerBeanEvent event) throws ManagerBeanException {
		OfferDetail detail = (OfferDetail)event.getTo();
		IManagerBean detailBean = BeanManager.getManagerBean(OfferDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(detailBean.getFieldName(IEntityAlias.OFFER_DETAIL_OFFER_ID), detail.getOffer().getId());
		criteria.addNotEqualExpression(detailBean.getFieldName(IEntityAlias.OFFER_DETAIL_ID), detail.getId());
		criteria.addGreaterThanOrEqualExpression(detailBean.getFieldName(IEntityAlias.OFFER_DETAIL_LINE), detail.getLine());
		criteria.addOrder(detailBean.getFieldName(IEntityAlias.OFFER_DETAIL_LINE));
		List<ITransferObject> list = detailBean.getList(criteria);
		int index = detail.getLine();
		for (ITransferObject to : list) {
			OfferDetail offerDetail = (OfferDetail)to;
			if (index == offerDetail.getLine()) {
				offerDetail.setLine(index + 1);
				detailBean.update(offerDetail);
				++index;
			}
		}
	}
	
	@Override
	public void beanUpdated(ManagerBeanEvent event) throws ManagerBeanException {
		if (!updating) {
			updating = true;

			OfferDetail detail = (OfferDetail)event.getTo();
			IManagerBean detailBean = BeanManager.getManagerBean(OfferDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(detailBean.getFieldName(IEntityAlias.OFFER_DETAIL_OFFER_ID), detail.getOffer().getId());
			criteria.addNotEqualExpression(detailBean.getFieldName(IEntityAlias.OFFER_DETAIL_ID), detail.getId());
			criteria.addEqualExpression(detailBean.getFieldName(IEntityAlias.OFFER_DETAIL_LINE), detail.getLine());
			if (detailBean.getCount(criteria) > 0) {
				criteria = new Criteria();
				criteria.addEqualExpression(detailBean.getFieldName(IEntityAlias.OFFER_DETAIL_OFFER_ID), detail.getOffer().getId());
				criteria.addNotEqualExpression(detailBean.getFieldName(IEntityAlias.OFFER_DETAIL_ID), detail.getId());
				criteria.addOrder(detailBean.getFieldName(IEntityAlias.OFFER_DETAIL_LINE));
				List<ITransferObject> list = detailBean.getList(criteria);
				int index = 1;
				for (ITransferObject to : list) {
					OfferDetail offerDetail = (OfferDetail)to;
					if (index == detail.getLine()) {
						++index;
					}
					offerDetail.setLine(index);
					detailBean.update(offerDetail);
					++index;
				}
			}

			updating = false;
		}
	}

	@Override
	public void beanRemoved(ManagerBeanEvent event) throws ManagerBeanException {
		OfferDetail detail = (OfferDetail)event.getTo();
		IManagerBean detailBean = BeanManager.getManagerBean(OfferDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(detailBean.getFieldName(IEntityAlias.OFFER_DETAIL_OFFER_ID), detail.getOffer().getId());
		criteria.addNotEqualExpression(detailBean.getFieldName(IEntityAlias.OFFER_DETAIL_ID), detail.getId());
		criteria.addGreaterThanOrEqualExpression(detailBean.getFieldName(IEntityAlias.OFFER_DETAIL_LINE), detail.getLine());
		criteria.addOrder(detailBean.getFieldName(IEntityAlias.OFFER_DETAIL_LINE));
		List<ITransferObject> list = detailBean.getList(criteria);
		int index = detail.getLine() + 1;
		for (ITransferObject to : list) {
			OfferDetail offerDetail = (OfferDetail)to;
			if (index == offerDetail.getLine()) {
				offerDetail.setLine(index - 1);
				detailBean.update(offerDetail);
				++ index;
			}
		}
	}

}
