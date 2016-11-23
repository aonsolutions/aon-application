package com.code.aon.warehouse.event;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.warehouse.DeliveryDetail;

public class DeliveryDetailBeanVetoListener extends ManagerBeanVetoListenerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Override
	public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		DeliveryDetail deliveryDetail = (DeliveryDetail) evt.getTo();
		setDefaultValues(deliveryDetail);
	}

	@Override
	public void vetoableBeanUpdated(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		DeliveryDetail deliveryDetail = (DeliveryDetail) evt.getTo();
		setDefaultValues(deliveryDetail);
	}

	private void setDefaultValues(DeliveryDetail deliveryDetail) {
		if (deliveryDetail.getDiscountExpression() == null || StringUtils.isBlank(deliveryDetail.getDiscountExpression().getDiscountExpr())) {
			deliveryDetail.setDiscountExpression(new DiscountExpression("0.0"));
		}
	}

}