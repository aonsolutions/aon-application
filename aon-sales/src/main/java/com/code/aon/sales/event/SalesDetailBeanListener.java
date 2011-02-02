package com.code.aon.sales.event;

import java.util.List;

import com.code.aon.commercial.Offer;
import com.code.aon.commercial.OfferDetail;
import com.code.aon.commercial.dao.ICommercialAlias;
import com.code.aon.commercial.enumeration.OfferDetailStatus;
import com.code.aon.commercial.enumeration.OfferStatus;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanListenerAdapter;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.sales.SalesDetail;
import com.code.aon.sales.dao.ISalesAlias;

public class SalesDetailBeanListener extends ManagerBeanListenerAdapter {

	private boolean updating = false;

	@Override
	public void beanInserted(ManagerBeanEvent event) throws ManagerBeanException {
		SalesDetail detail = (SalesDetail)event.getTo();
		IManagerBean detailBean = BeanManager.getManagerBean(SalesDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(detailBean.getFieldName(ISalesAlias.SALES_DETAIL_SALES_ID), detail.getSales().getId());
		criteria.addExpression(ExpressionUtilities.getNotEqualExpression(detailBean.getFieldName(ISalesAlias.SALES_DETAIL_ID), detail.getId()));
		criteria.addGreaterThanOrEqualExpression(detailBean.getFieldName(ISalesAlias.SALES_DETAIL_LINE), detail.getLine());
		criteria.addOrder(detailBean.getFieldName(ISalesAlias.SALES_DETAIL_LINE));
		List<ITransferObject> list = detailBean.getList(criteria);
		int index = detail.getLine();
		for (ITransferObject to : list) {
			SalesDetail salesDetail = (SalesDetail)to;
			if (index == salesDetail.getLine()) {
				salesDetail.setLine(index + 1);
				detailBean.update(salesDetail);
				++index;
			}
		}
	}
	
	@Override
	public void beanUpdated(ManagerBeanEvent event) throws ManagerBeanException {
		if (!updating) {
			updating = true;

			SalesDetail detail = (SalesDetail)event.getTo();
			IManagerBean detailBean = BeanManager.getManagerBean(SalesDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(detailBean.getFieldName(ISalesAlias.SALES_DETAIL_SALES_ID), detail.getSales().getId());
			criteria.addExpression(ExpressionUtilities.getNotEqualExpression(detailBean.getFieldName(ISalesAlias.SALES_DETAIL_ID), detail.getId()));
			criteria.addEqualExpression(detailBean.getFieldName(ISalesAlias.SALES_DETAIL_LINE), detail.getLine());
			if (detailBean.getCount(criteria) > 0) {
				criteria = new Criteria();
				criteria.addEqualExpression(detailBean.getFieldName(ISalesAlias.SALES_DETAIL_SALES_ID), detail.getSales().getId());
				criteria.addExpression(ExpressionUtilities.getNotEqualExpression(detailBean.getFieldName(ISalesAlias.SALES_DETAIL_ID), detail.getId()));
				criteria.addOrder(detailBean.getFieldName(ISalesAlias.SALES_DETAIL_LINE));
				List<ITransferObject> list = detailBean.getList(criteria);
				int index = 1;
				for (ITransferObject to : list) {
					SalesDetail salesDetail = (SalesDetail)to;
					if (index == detail.getLine()) {
						++index;
					}
					salesDetail.setLine(index);
					detailBean.update(salesDetail);
					++index;
				}
			}

			updating = false;
		}
	}

	@Override
	public void beanRemoved(ManagerBeanEvent evt) throws ManagerBeanException {
		SalesDetail detail = (SalesDetail)evt.getTo();
		if (detail.getOfferDetail() != null && detail.getOfferDetail().getId() != null) {
			updateRelatedOffer(detail.getOfferDetail());
		}

		IManagerBean detailBean = BeanManager.getManagerBean(SalesDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(detailBean.getFieldName(ISalesAlias.SALES_DETAIL_SALES_ID), detail.getSales().getId());
		criteria.addExpression(ExpressionUtilities.getNotEqualExpression(detailBean.getFieldName(ISalesAlias.SALES_DETAIL_ID), detail.getId()));
		criteria.addGreaterThanOrEqualExpression(detailBean.getFieldName(ISalesAlias.SALES_DETAIL_LINE), detail.getLine());
		criteria.addOrder(detailBean.getFieldName(ISalesAlias.SALES_DETAIL_LINE));
		List<ITransferObject> list = detailBean.getList(criteria);
		int index = detail.getLine() + 1;
		for (ITransferObject to : list) {
			SalesDetail salesDetail = (SalesDetail)to;
			if (index == salesDetail.getLine()) {
				salesDetail.setLine(index - 1);
				detailBean.update(salesDetail);
				++ index;
			}
		}
	}

	private void updateRelatedOffer(OfferDetail offerDetail) throws ManagerBeanException {
		IManagerBean offerDetailBean = BeanManager.getManagerBean(OfferDetail.class);
		offerDetail.setStatus(OfferDetailStatus.PENDING);
		offerDetailBean.update(offerDetail);

		Criteria criteria = new Criteria();
		criteria.addEqualExpression(offerDetailBean.getFieldName(ICommercialAlias.OFFER_DETAIL_OFFER_ID), offerDetail.getOffer().getId());
		criteria.addNotNullExpression(offerDetailBean.getFieldName(ICommercialAlias.OFFER_DETAIL_ITEM_ID));
		criteria.addEqualExpression(offerDetailBean.getFieldName(ICommercialAlias.OFFER_DETAIL_STATUS), OfferDetailStatus.ON_SALE);
		if (offerDetailBean.getCount(criteria) == 0) {
			IManagerBean offerBean = BeanManager.getManagerBean(Offer.class);
			Offer offer = offerDetail.getOffer();
			offer.setStatus(OfferStatus.PENDING);
			offerBean.update(offer);
		}
	}

}
