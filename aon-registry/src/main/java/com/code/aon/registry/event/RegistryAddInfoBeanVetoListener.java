package com.code.aon.registry.event;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.registry.RegistryAddInfo;

public class RegistryAddInfoBeanVetoListener extends ManagerBeanVetoListenerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

    @Override
    public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
    	RegistryAddInfo to = (RegistryAddInfo)evt.getTo();
    	to.setAttribute(StringUtils.trim(to.getAttribute()));
    	to.setValue(StringUtils.trim(to.getValue()));
    }

	@Override
	public void vetoableBeanUpdated(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		RegistryAddInfo to = (RegistryAddInfo)evt.getTo();
    	to.setAttribute(StringUtils.trim(to.getAttribute()));
    	to.setValue(StringUtils.trim(to.getValue()));
	}

}