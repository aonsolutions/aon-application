package com.code.aon.ui.tas.event;

import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.tas.TasItem;

public class TasItemBeanVetoListener extends ManagerBeanVetoListenerAdapter {
	
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
		if (BLANK.equals(to.getPrivateCode())){
			to.setPrivateCode(null);
		}
	}
	
	private static String BLANK = "";
	
}
