package com.code.aon.registry.event;

import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.enumeration.AddressType;

public class RegistryAddressBeanVetoListener extends ManagerBeanVetoListenerAdapter {

    @Override
    public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
    	RegistryAddress to = (RegistryAddress) evt.getTo();
    	if (to.getAddressType() == null) {
    		to.setAddressType(AddressType.DELEGATION);
    	}
    }

    @Override
    public void vetoableBeanUpdated(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
    }

}
