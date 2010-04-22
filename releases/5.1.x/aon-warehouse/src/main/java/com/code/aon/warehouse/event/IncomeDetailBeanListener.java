package com.code.aon.warehouse.event;

import java.util.List;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanListenerAdapter;
import com.code.aon.purchase.Purchase;
import com.code.aon.purchase.PurchaseDetail;
import com.code.aon.purchase.enumeration.PurchaseDetailStatus;
import com.code.aon.purchase.enumeration.PurchaseStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.warehouse.IncomeDetail;
import com.code.aon.warehouse.dao.IWarehouseAlias;

public class IncomeDetailBeanListener extends ManagerBeanListenerAdapter {

	private boolean updating = false;

	@Override
	public void beanInserted(ManagerBeanEvent event) throws ManagerBeanException {
		IncomeDetail detail = (IncomeDetail)event.getTo();
		IManagerBean detailBean = BeanManager.getManagerBean(IncomeDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(detailBean.getFieldName(IWarehouseAlias.INCOME_DETAIL_INCOME_ID), detail.getIncome().getId());
		criteria.addExpression(ExpressionUtilities.getNotEqualExpression(detailBean.getFieldName(IWarehouseAlias.INCOME_DETAIL_ID), detail.getId()));
		criteria.addGreaterThanOrEqualExpression(detailBean.getFieldName(IWarehouseAlias.INCOME_DETAIL_LINE), detail.getLine());
		criteria.addOrder(detailBean.getFieldName(IWarehouseAlias.INCOME_DETAIL_LINE));
		List<ITransferObject> list = detailBean.getList(criteria);
		int index = detail.getLine();
		for (ITransferObject to : list) {
			IncomeDetail incomeDetail = (IncomeDetail)to;
			if (index == incomeDetail.getLine()) {
				incomeDetail.setLine(index + 1);
				detailBean.update(incomeDetail);
				++index;
			}
		}
	}
	
	@Override
	public void beanUpdated(ManagerBeanEvent event) throws ManagerBeanException {
		if (!updating) {
			updating = true;

			IncomeDetail detail = (IncomeDetail)event.getTo();
			IManagerBean detailBean = BeanManager.getManagerBean(IncomeDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(detailBean.getFieldName(IWarehouseAlias.INCOME_DETAIL_INCOME_ID), detail.getIncome().getId());
			criteria.addExpression(ExpressionUtilities.getNotEqualExpression(detailBean.getFieldName(IWarehouseAlias.INCOME_DETAIL_ID), detail.getId()));
			criteria.addEqualExpression(detailBean.getFieldName(IWarehouseAlias.INCOME_DETAIL_LINE), detail.getLine());
			if (detailBean.getCount(criteria) > 0) {
				criteria = new Criteria();
				criteria.addEqualExpression(detailBean.getFieldName(IWarehouseAlias.INCOME_DETAIL_INCOME_ID), detail.getIncome().getId());
				criteria.addExpression(ExpressionUtilities.getNotEqualExpression(detailBean.getFieldName(IWarehouseAlias.INCOME_DETAIL_ID), detail.getId()));
				criteria.addOrder(detailBean.getFieldName(IWarehouseAlias.INCOME_DETAIL_LINE));
				List<ITransferObject> list = detailBean.getList(criteria);
				int index = 1;
				for (ITransferObject to : list) {
					IncomeDetail incomeDetail = (IncomeDetail)to;
					if (index == detail.getLine()) {
						++index;
					}
					incomeDetail.setLine(index);
					detailBean.update(incomeDetail);
					++index;
				}
			}

			updating = false;
		}
	}

	@Override
	public void beanRemoved(ManagerBeanEvent evt) throws ManagerBeanException {
		IncomeDetail detail = (IncomeDetail)evt.getTo();
		if (detail.getPurchaseDetail() != null && detail.getPurchaseDetail().getId() != null) {
			updateRelatedPurchase(detail.getPurchaseDetail());
		}

		IManagerBean detailBean = BeanManager.getManagerBean(IncomeDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(detailBean.getFieldName(IWarehouseAlias.INCOME_DETAIL_INCOME_ID), detail.getIncome().getId());
		criteria.addExpression(ExpressionUtilities.getNotEqualExpression(detailBean.getFieldName(IWarehouseAlias.INCOME_DETAIL_ID), detail.getId()));
		criteria.addGreaterThanOrEqualExpression(detailBean.getFieldName(IWarehouseAlias.INCOME_DETAIL_LINE), detail.getLine());
		criteria.addOrder(detailBean.getFieldName(IWarehouseAlias.INCOME_DETAIL_LINE));
		List<ITransferObject> list = detailBean.getList(criteria);
		int index = detail.getLine() + 1;
		for (ITransferObject to : list) {
			IncomeDetail incomeDetail = (IncomeDetail)to;
			if (index == incomeDetail.getLine()) {
				incomeDetail.setLine(index - 1);
				detailBean.update(incomeDetail);
				++ index;
			}
		}
	}

	private void updateRelatedPurchase(PurchaseDetail purchaseDetail) throws ManagerBeanException {
		IManagerBean purchaseDetailBean = BeanManager.getManagerBean(PurchaseDetail.class);
		purchaseDetail.setDelivered(0);
		purchaseDetail.setStatus(PurchaseDetailStatus.PENDING);
		purchaseDetailBean.update(purchaseDetail);

		IManagerBean purchaseBean = BeanManager.getManagerBean(Purchase.class);
		Purchase purchase = purchaseDetail.getPurchase();
		purchase.setStatus(PurchaseStatus.PENDING);
		purchaseBean.update(purchase);
	}

}
