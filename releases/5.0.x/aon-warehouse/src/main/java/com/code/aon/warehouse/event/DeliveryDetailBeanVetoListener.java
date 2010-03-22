package com.code.aon.warehouse.event;

import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.warehouse.DeliveryDetail;

public class DeliveryDetailBeanVetoListener extends ManagerBeanVetoListenerAdapter {

	@Override
	public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		DeliveryDetail deliveryDetail = (DeliveryDetail) evt.getTo();
		setDefaultValues(deliveryDetail);
	}

	private void setDefaultValues(DeliveryDetail deliveryDetail) {
		if (deliveryDetail.getDiscountExpression() == null || deliveryDetail.getDiscountExpression().getDiscountExpr() == null) {
			deliveryDetail.setDiscountExpression(new DiscountExpression("0.0"));
		}
	}

}