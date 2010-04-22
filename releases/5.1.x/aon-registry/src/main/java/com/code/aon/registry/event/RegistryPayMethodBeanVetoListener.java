package com.code.aon.registry.event;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.registry.RegistryPayMethod;

public class RegistryPayMethodBeanVetoListener extends ManagerBeanVetoListenerAdapter {

    @Override
    public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
    	RegistryPayMethod to = (RegistryPayMethod)evt.getTo();
    	if (to.getNumberOfPayments() == 0) {
    		to.setNumberOfPayments(1);
    	}
    	if (to.getPaymentDays() == null) {
    		to.setPaymentDays(StringUtils.EMPTY);
    	}
    }

    @Override
    public void vetoableBeanUpdated(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
    	RegistryPayMethod to = (RegistryPayMethod)evt.getTo();
    	if (to.getNumberOfPayments() == 0) {
    		to.setNumberOfPayments(1);
    	}
    	if (to.getPaymentDays() == null) {
    		to.setPaymentDays(StringUtils.EMPTY);
    	}
    }

}
