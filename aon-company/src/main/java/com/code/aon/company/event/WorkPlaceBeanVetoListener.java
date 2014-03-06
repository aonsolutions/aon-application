package com.code.aon.company.event;

import com.code.aon.common.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.company.Enterprise;
import com.code.aon.company.WorkPlace;

public class WorkPlaceBeanVetoListener extends ManagerBeanVetoListenerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Override
	public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		WorkPlace workPlace =  (WorkPlace)evt.getTo();
		try {
			if (workPlace.getScope() == null || workPlace.getScope().getId() == null) {
				Enterprise enterprise = workPlace.getEnterprise();
				if (enterprise != null && enterprise.getId() != null) {
					enterprise = (Enterprise)BeanManager.getManagerBean(Enterprise.class).get(enterprise.getId());
					workPlace.setScope(enterprise.getScope());
				}
			}
		} catch (ManagerBeanException ex) {
			throw new ManagerBeanVetoListenerException(ex.getMessage(), ex);
		}
	}

}
