package com.code.aon.warehouse.event;

import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.warehouse.Delivery;
import com.code.aon.warehouse.enumeration.DeliveryStatus;

public class DeliveryBeanVetoListener extends ManagerBeanVetoListenerAdapter {

	@Override
	public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		Delivery delivery = (Delivery) evt.getTo();
		setDefaultValues(delivery);
	}

	private void setDefaultValues(Delivery delivery) {
		if (delivery.getSecurityLevel() == null) {
			delivery.setSecurityLevel(SecurityLevel.OFFICIAL);
		}
		if (delivery.getStatus() == null) {
			delivery.setStatus(DeliveryStatus.PENDING);
		}
		if (delivery.getScope() == null || delivery.getScope().getId() == null) {
			delivery.setScope(delivery.getCustomer().getScope());
		}
	}

}