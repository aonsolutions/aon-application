package com.code.aon.tas.event;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.tas.TasItem;

public class TasItemBeanVetoListener extends ManagerBeanVetoListenerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	@Override
	public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		nullPrivateCodeifBlank(evt);
	}
	
	@Override
	public void vetoableBeanUpdated(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		nullPrivateCodeifBlank(evt);
	}
	
	private void nullPrivateCodeifBlank(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		TasItem to = (TasItem)evt.getTo();
		if (StringUtils.isBlank(to.getPrivateCode())){
			to.setPrivateCode(null);
		}
	}
	
}
