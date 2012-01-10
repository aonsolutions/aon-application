package com.code.aon.company.event;

import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.company.WorkPlace;

public class WorkPlaceBeanVetoListener extends ManagerBeanVetoListenerAdapter {

	@Override
	public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		WorkPlace workPlace =  (WorkPlace)evt.getTo();
		if (workPlace.getScope() == null || workPlace.getScope().getId() == null) {
			workPlace.setScope(workPlace.getEnterprise().getScope());
		}
	}

}
