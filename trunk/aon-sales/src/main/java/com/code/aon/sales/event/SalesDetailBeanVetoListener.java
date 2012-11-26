package com.code.aon.sales.event;

import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.sales.SalesDetail;
import com.code.aon.sales.enumeration.SalesDetailStatus;

public class SalesDetailBeanVetoListener extends ManagerBeanVetoListenerAdapter {

	@Override
	public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		SalesDetail salesDetail = (SalesDetail) evt.getTo();
		setDefaultValues(salesDetail);
		checkSalesDetail(salesDetail);
	}

	@Override
	public void vetoableBeanUpdated(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		SalesDetail salesDetail = (SalesDetail) evt.getTo();
		checkSalesDetail(salesDetail);
	}

	private void setDefaultValues(SalesDetail salesDetail) {
		if (salesDetail.getDiscountExpression() == null || salesDetail.getDiscountExpression().getDiscountExpr() == null) {
			salesDetail.setDiscountExpression(new DiscountExpression("0.0"));
		}
		if (salesDetail.getStatus() == null) {
			salesDetail.setStatus(SalesDetailStatus.PENDING);
		}
	}

	private void checkSalesDetail(SalesDetail salesDetail) throws ManagerBeanVetoListenerException {
		if (salesDetail.getQuantity() < 0) {
			throw new ManagerBeanVetoListenerException("La Cantidad del Pedido no puede ser negativa.");
		}

		if (salesDetail.getDelivered() == 0) {
			salesDetail.setStatus(SalesDetailStatus.PENDING);
		} else if (salesDetail.getDelivered() >= salesDetail.getQuantity()) {
			salesDetail.setQuantity(salesDetail.getDelivered());
			salesDetail.setStatus(SalesDetailStatus.SETTLED);
		} else {
			salesDetail.setStatus(SalesDetailStatus.PARTIAL_SETTLED);
		}
	}

}