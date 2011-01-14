package com.code.aon.purchase.event;

import java.util.List;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanListenerAdapter;
import com.code.aon.purchase.PurchaseDetail;
import com.code.aon.purchase.dao.IPurchaseAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionUtilities;

public class PurchaseDetailBeanListener extends ManagerBeanListenerAdapter {

	private boolean updating = false;

	@Override
	public void beanInserted(ManagerBeanEvent event) throws ManagerBeanException {
		PurchaseDetail detail = (PurchaseDetail)event.getTo();
		IManagerBean detailBean = BeanManager.getManagerBean(PurchaseDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(detailBean.getFieldName(IPurchaseAlias.PURCHASE_DETAIL_PURCHASE_ID), detail.getPurchase().getId());
		criteria.addExpression(ExpressionUtilities.getNotEqualExpression(detailBean.getFieldName(IPurchaseAlias.PURCHASE_DETAIL_ID), detail.getId()));
		criteria.addGreaterThanOrEqualExpression(detailBean.getFieldName(IPurchaseAlias.PURCHASE_DETAIL_LINE), detail.getLine());
		criteria.addOrder(detailBean.getFieldName(IPurchaseAlias.PURCHASE_DETAIL_LINE));
		List<ITransferObject> list = detailBean.getList(criteria);
		int index = detail.getLine();
		for (ITransferObject to : list) {
			PurchaseDetail purchaseDetail = (PurchaseDetail)to;
			if (index == purchaseDetail.getLine()) {
				purchaseDetail.setLine(index + 1);
				detailBean.update(purchaseDetail);
				++index;
			}
		}
	}
	
	@Override
	public void beanUpdated(ManagerBeanEvent event) throws ManagerBeanException {
		if (!updating) {
			updating = true;

			PurchaseDetail detail = (PurchaseDetail)event.getTo();
			IManagerBean detailBean = BeanManager.getManagerBean(PurchaseDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(detailBean.getFieldName(IPurchaseAlias.PURCHASE_DETAIL_PURCHASE_ID), detail.getPurchase().getId());
			criteria.addExpression(ExpressionUtilities.getNotEqualExpression(detailBean.getFieldName(IPurchaseAlias.PURCHASE_DETAIL_ID), detail.getId()));
			criteria.addEqualExpression(detailBean.getFieldName(IPurchaseAlias.PURCHASE_DETAIL_LINE), detail.getLine());
			if (detailBean.getCount(criteria) > 0) {
				criteria = new Criteria();
				criteria.addEqualExpression(detailBean.getFieldName(IPurchaseAlias.PURCHASE_DETAIL_PURCHASE_ID), detail.getPurchase().getId());
				criteria.addExpression(ExpressionUtilities.getNotEqualExpression(detailBean.getFieldName(IPurchaseAlias.PURCHASE_DETAIL_ID), detail.getId()));
				criteria.addOrder(detailBean.getFieldName(IPurchaseAlias.PURCHASE_DETAIL_LINE));
				List<ITransferObject> list = detailBean.getList(criteria);
				int index = 1;
				for (ITransferObject to : list) {
					PurchaseDetail purchaseDetail = (PurchaseDetail)to;
					if (index == detail.getLine()) {
						++index;
					}
					purchaseDetail.setLine(index);
					detailBean.update(purchaseDetail);
					++index;
				}
			}

			updating = false;
		}
	}

	@Override
	public void beanRemoved(ManagerBeanEvent evt) throws ManagerBeanException {
		PurchaseDetail detail = (PurchaseDetail)evt.getTo();
		IManagerBean detailBean = BeanManager.getManagerBean(PurchaseDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(detailBean.getFieldName(IPurchaseAlias.PURCHASE_DETAIL_PURCHASE_ID), detail.getPurchase().getId());
		criteria.addExpression(ExpressionUtilities.getNotEqualExpression(detailBean.getFieldName(IPurchaseAlias.PURCHASE_DETAIL_ID), detail.getId()));
		criteria.addGreaterThanOrEqualExpression(detailBean.getFieldName(IPurchaseAlias.PURCHASE_DETAIL_LINE), detail.getLine());
		criteria.addOrder(detailBean.getFieldName(IPurchaseAlias.PURCHASE_DETAIL_LINE));
		List<ITransferObject> list = detailBean.getList(criteria);
		int index = detail.getLine() + 1;
		for (ITransferObject to : list) {
			PurchaseDetail purchaseDetail = (PurchaseDetail)to;
			if (index == purchaseDetail.getLine()) {
				purchaseDetail.setLine(index - 1);
				detailBean.update(purchaseDetail);
				++ index;
			}
		}
	}

}
