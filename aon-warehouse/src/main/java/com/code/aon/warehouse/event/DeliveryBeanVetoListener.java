package com.code.aon.warehouse.event;

import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.warehouse.Delivery;

public class DeliveryBeanVetoListener extends ManagerBeanVetoListenerAdapter {

	@Override
	public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		Delivery delivery = (Delivery) evt.getTo();
		if (delivery.getScope() == null || delivery.getScope().getId() == null) {
			delivery.setScope(delivery.getCustomer().getScope());
		}
	}

}