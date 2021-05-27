package com.code.aon.groupware.event;

import com.code.aon.AonVersion;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.groupware.CostProfile;
import com.code.aon.groupware.DailyTracking;

public class DailyTrackingBeanVetoListener extends ManagerBeanVetoListenerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Override
	public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		DailyTracking to = (DailyTracking) evt.getTo();
		to.setCost(0.0);
		if (to.getTaskHolder() != null && to.getTaskHolder().getCostProfile() != null) {
			CostProfile costProfile = to.getTaskHolder().getCostProfile();
			to.setCost(costProfile.getCost()); 
		}
	}
	
}
