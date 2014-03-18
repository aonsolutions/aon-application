package com.code.aon.warehouse.event;

import java.util.Date;

import com.code.aon.AonVersion;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.warehouse.Delivery;
import com.code.aon.warehouse.enumeration.DeliveryStatus;

public class DeliveryBeanVetoListener extends ManagerBeanVetoListenerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Override
	public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		Delivery delivery = (Delivery) evt.getTo();
		setDefaultValues(delivery);
		checkDelivery(delivery);
	}

	@Override
	public void vetoableBeanUpdated(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		Delivery delivery = (Delivery) evt.getTo();
		checkDelivery(delivery);
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

	private void checkDelivery(Delivery delivery) throws ManagerBeanVetoListenerException {
		int thisYear = CommonUtil.getYear(new Date());
		int deliveryYear = CommonUtil.getYear(delivery.getIssueTime());
		if (deliveryYear < (thisYear-5) || deliveryYear > (thisYear+1)) {
			throw new ManagerBeanVetoListenerException("La Fecha del Albaran no es correcta.");
		}
	}

}