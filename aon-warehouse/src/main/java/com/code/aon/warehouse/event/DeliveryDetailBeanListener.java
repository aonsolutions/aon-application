package com.code.aon.warehouse.event;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanListenerAdapter;
import com.code.aon.sales.Sales;
import com.code.aon.sales.SalesDetail;
import com.code.aon.sales.enumeration.SalesDetailStatus;
import com.code.aon.sales.enumeration.SalesStatus;
import com.code.aon.warehouse.DeliveryDetail;

public class DeliveryDetailBeanListener extends ManagerBeanListenerAdapter {

	@Override
	public void beanRemoved(ManagerBeanEvent evt) throws ManagerBeanException {
		DeliveryDetail deliveryDetail = (DeliveryDetail)evt.getTo();
		if (deliveryDetail.getSalesDetail() != null && deliveryDetail.getSalesDetail().getId() != null) {
			updateRelatedSales(deliveryDetail.getSalesDetail());
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
