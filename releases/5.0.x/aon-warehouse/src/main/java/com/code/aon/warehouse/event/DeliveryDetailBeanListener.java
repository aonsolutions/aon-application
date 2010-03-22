package com.code.aon.warehouse.event;

import java.util.List;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanListenerAdapter;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.sales.Sales;
import com.code.aon.sales.SalesDetail;
import com.code.aon.sales.enumeration.SalesDetailStatus;
import com.code.aon.sales.enumeration.SalesStatus;
import com.code.aon.warehouse.DeliveryDetail;
import com.code.aon.warehouse.dao.IWarehouseAlias;

public class DeliveryDetailBeanListener extends ManagerBeanListenerAdapter {

	private boolean updating = false;

	@Override
	public void beanInserted(ManagerBeanEvent event) throws ManagerBeanException {
		DeliveryDetail detail = (DeliveryDetail)event.getTo();
		IManagerBean detailBean = BeanManager.getManagerBean(DeliveryDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(detailBean.getFieldName(IWarehouseAlias.DELIVERY_DETAIL_DELIVERY_ID), detail.getDelivery().getId());
		criteria.addExpression(ExpressionUtilities.getNotEqualExpression(detailBean.getFieldName(IWarehouseAlias.DELIVERY_DETAIL_ID), detail.getId()));
		criteria.addGreaterThanOrEqualExpression(detailBean.getFieldName(IWarehouseAlias.DELIVERY_DETAIL_LINE), detail.getLine());
		criteria.addOrder(detailBean.getFieldName(IWarehouseAlias.DELIVERY_DETAIL_LINE));
		List<ITransferObject> list = detailBean.getList(criteria);
		int index = detail.getLine();
		for (ITransferObject to : list) {
			DeliveryDetail deliveryDetail = (DeliveryDetail)to;
			if (index == deliveryDetail.getLine()) {
				deliveryDetail.setLine(index + 1);
				detailBean.update(deliveryDetail);
				++index;
			}
		}
	}
	
	@Override
	public void beanUpdated(ManagerBeanEvent event) throws ManagerBeanException {
		if (!updating) {
			updating = true;

			DeliveryDetail detail = (DeliveryDetail)event.getTo();
			IManagerBean detailBean = BeanManager.getManagerBean(DeliveryDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(detailBean.getFieldName(IWarehouseAlias.DELIVERY_DETAIL_DELIVERY_ID), detail.getDelivery().getId());
			criteria.addExpression(ExpressionUtilities.getNotEqualExpression(detailBean.getFieldName(IWarehouseAlias.DELIVERY_DETAIL_ID), detail.getId()));
			criteria.addEqualExpression(detailBean.getFieldName(IWarehouseAlias.DELIVERY_DETAIL_LINE), detail.getLine());
			if (detailBean.getCount(criteria) > 0) {
				criteria = new Criteria();
				criteria.addEqualExpression(detailBean.getFieldName(IWarehouseAlias.DELIVERY_DETAIL_DELIVERY_ID), detail.getDelivery().getId());
				criteria.addExpression(ExpressionUtilities.getNotEqualExpression(detailBean.getFieldName(IWarehouseAlias.DELIVERY_DETAIL_ID), detail.getId()));
				criteria.addOrder(detailBean.getFieldName(IWarehouseAlias.DELIVERY_DETAIL_LINE));
				List<ITransferObject> list = detailBean.getList(criteria);
				int index = 1;
				for (ITransferObject to : list) {
					DeliveryDetail deliveryDetail = (DeliveryDetail)to;
					if (index == detail.getLine()) {
						++index;
					}
					deliveryDetail.setLine(index);
					detailBean.update(deliveryDetail);
					++index;
				}
			}

			updating = false;
		}
	}

	@Override
	public void beanRemoved(ManagerBeanEvent evt) throws ManagerBeanException {
		DeliveryDetail detail = (DeliveryDetail)evt.getTo();
		if (detail.getSalesDetail() != null && detail.getSalesDetail().getId() != null) {
			updateRelatedSales(detail.getSalesDetail());
		}

		IManagerBean detailBean = BeanManager.getManagerBean(DeliveryDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(detailBean.getFieldName(IWarehouseAlias.DELIVERY_DETAIL_DELIVERY_ID), detail.getDelivery().getId());
		criteria.addExpression(ExpressionUtilities.getNotEqualExpression(detailBean.getFieldName(IWarehouseAlias.DELIVERY_DETAIL_ID), detail.getId()));
		criteria.addGreaterThanOrEqualExpression(detailBean.getFieldName(IWarehouseAlias.DELIVERY_DETAIL_LINE), detail.getLine());
		criteria.addOrder(detailBean.getFieldName(IWarehouseAlias.DELIVERY_DETAIL_LINE));
		List<ITransferObject> list = detailBean.getList(criteria);
		int index = detail.getLine() + 1;
		for (ITransferObject to : list) {
			DeliveryDetail deliveryDetail = (DeliveryDetail)to;
			if (index == deliveryDetail.getLine()) {
				deliveryDetail.setLine(index - 1);
				detailBean.update(deliveryDetail);
				++ index;
			}
		}
	}

	private void updateRelatedSales(SalesDetail salesDetail) throws ManagerBeanException {
		IManagerBean salesDetailBean = BeanManager.getManagerBean(SalesDetail.class);
		salesDetail.setDelivered(0);
		salesDetail.setStatus(SalesDetailStatus.PENDING);
		salesDetailBean.update(salesDetail);

		IManagerBean salesBean = BeanManager.getManagerBean(Sales.class);
		Sales sales = salesDetail.getSales();
		sales.setStatus(SalesStatus.PENDING);
		salesBean.update(sales);
	}

}
