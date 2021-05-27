package com.code.aon.sales.event;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.code.aon.commercial.Offer;
import com.code.aon.commercial.OfferDetail;
import com.code.aon.commercial.enumeration.OfferDetailStatus;
import com.code.aon.commercial.enumeration.OfferStatus;
import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanListenerAdapter;
import com.code.aon.ql.Criteria;
import com.code.aon.sales.SalesDetail;
import com.esferalia.aon.entity.IEntityAlias;

public class SalesDetailBeanListener extends ManagerBeanListenerAdapter {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private boolean updating = false;

	@Override
	public void beanInserted(ManagerBeanEvent event) throws ManagerBeanException {
		SalesDetail detail = (SalesDetail)event.getTo();
		IManagerBean detailBean = BeanManager.getManagerBean(SalesDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(detailBean.getFieldName(IEntityAlias.SALES_DETAIL_SALES_ID), detail.getSales().getId());
		criteria.addNotEqualExpression(detailBean.getFieldName(IEntityAlias.SALES_DETAIL_ID), detail.getId());
		criteria.addGreaterThanOrEqualExpression(detailBean.getFieldName(IEntityAlias.SALES_DETAIL_LINE), detail.getLine());
		criteria.addOrder(detailBean.getFieldName(IEntityAlias.SALES_DETAIL_LINE));
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
			criteria.addEqualExpression(detailBean.getFieldName(IEntityAlias.SALES_DETAIL_SALES_ID), detail.getSales().getId());
			criteria.addNotEqualExpression(detailBean.getFieldName(IEntityAlias.SALES_DETAIL_ID), detail.getId());
			criteria.addEqualExpression(detailBean.getFieldName(IEntityAlias.SALES_DETAIL_LINE), detail.getLine());
			if (detailBean.getCount(criteria) > 0) {
				criteria = new Criteria();
				criteria.addEqualExpression(detailBean.getFieldName(IEntityAlias.SALES_DETAIL_SALES_ID), detail.getSales().getId());
				criteria.addNotEqualExpression(detailBean.getFieldName(IEntityAlias.SALES_DETAIL_ID), detail.getId());
				criteria.addOrder(detailBean.getFieldName(IEntityAlias.SALES_DETAIL_LINE));
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
		if (!detail.isSkipOfferUpdate() && detail.getOfferDetail() != null && detail.getOfferDetail().getId() != null) {
			updateRelatedOffer(detail, detail.getOfferDetail());
		}

		IManagerBean detailBean = BeanManager.getManagerBean(SalesDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(detailBean.getFieldName(IEntityAlias.SALES_DETAIL_SALES_ID), detail.getSales().getId());
		criteria.addNotEqualExpression(detailBean.getFieldName(IEntityAlias.SALES_DETAIL_ID), detail.getId());
		criteria.addGreaterThanOrEqualExpression(detailBean.getFieldName(IEntityAlias.SALES_DETAIL_LINE), detail.getLine());
		criteria.addOrder(detailBean.getFieldName(IEntityAlias.SALES_DETAIL_LINE));
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

	private void updateRelatedOffer(SalesDetail salesDetail, OfferDetail offerDetail) throws ManagerBeanException {
		IManagerBean detailBean = BeanManager.getManagerBean(SalesDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(detailBean.getFieldName(IEntityAlias.SALES_DETAIL_OFFER_DETAIL_ID), offerDetail.getId());
		if (detailBean.getCount(criteria) == 0) {
			IManagerBean offerDetailBean = BeanManager.getManagerBean(OfferDetail.class);
			offerDetail.setStatus(OfferDetailStatus.PENDING);
			offerDetailBean.update(offerDetail);
			
			criteria = new Criteria();
			criteria.addEqualExpression(offerDetailBean.getFieldName(IEntityAlias.OFFER_DETAIL_OFFER_ID), offerDetail.getOffer().getId());
			criteria.addNotNullExpression(offerDetailBean.getFieldName(IEntityAlias.OFFER_DETAIL_ITEM_ID));
			criteria.addEqualExpression(offerDetailBean.getFieldName(IEntityAlias.OFFER_DETAIL_STATUS), OfferDetailStatus.ON_SALE);
			if (offerDetailBean.getCount(criteria) == 0) {
				IManagerBean offerBean = BeanManager.getManagerBean(Offer.class);
				Offer offer = offerDetail.getOffer();
				offer.setStatus(OfferStatus.PENDING);
				offerBean.update(offer);
			}
		} else {
			if (!StringUtils.isBlank(salesDetail.getItem().getSerialNumber())) {
				criteria = new Criteria();
				criteria.addEqualExpression(detailBean.getFieldName(IEntityAlias.SALES_DETAIL_SALES_ID), salesDetail.getSales().getId());
				criteria.addEqualExpression(detailBean.getFieldName(IEntityAlias.SALES_DETAIL_OFFER_DETAIL_ID), offerDetail.getId());
				criteria.addEqualExpression(detailBean.getFieldName(IEntityAlias.SALES_DETAIL_ITEM_PRODUCT_ID), salesDetail.getItem().getProduct().getId());
				criteria.addNullExpression(detailBean.getFieldName(IEntityAlias.SALES_DETAIL_ITEM_SERIAL_NUMBER));
				for (ITransferObject ito : detailBean.getList(criteria)) {
					SalesDetail wildCardDetail = (SalesDetail)ito;
					wildCardDetail.setQuantity(wildCardDetail.getQuantity() + salesDetail.getQuantity());
					detailBean.update(wildCardDetail);
					break;
				}
			}
		}
	}

}
