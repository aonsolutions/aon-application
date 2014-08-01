package com.code.aon.registry.event;

import com.code.aon.AonVersion;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.registry.RegistryNote;

public class RegistryNoteBeanVetoListener extends ManagerBeanVetoListenerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

    @Override
    public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
    	RegistryNote to = (RegistryNote)evt.getTo();
		if (to.getSecurityLevel() == null) {
			to.setSecurityLevel(SecurityLevel.OFFICIAL);
		}
    }

    @Override
    public void vetoableBeanUpdated(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
    	RegistryNote to = (RegistryNote)evt.getTo();
		if (to.getSecurityLevel() == null) {
			to.setSecurityLevel(SecurityLevel.OFFICIAL);
		}
    }

}
