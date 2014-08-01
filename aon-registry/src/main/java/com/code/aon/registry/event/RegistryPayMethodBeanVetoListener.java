package com.code.aon.registry.event;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.registry.RegistryPayMethod;

public class RegistryPayMethodBeanVetoListener extends ManagerBeanVetoListenerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

    @Override
    public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
    	RegistryPayMethod to = (RegistryPayMethod)evt.getTo();
    	ensureParams(to);
    	validatePaymentDays(to);
    }

    @Override
    public void vetoableBeanUpdated(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
    	RegistryPayMethod to = (RegistryPayMethod)evt.getTo();
    	ensureParams(to);
    	validatePaymentDays(to);
    }

    private void validatePaymentDays(RegistryPayMethod to) throws ManagerBeanVetoListenerException{
    	if (StringUtils.isNotEmpty( to.getPaymentDays() )) {
    		for (int day : to.getPaymentDaysArray()) {
    			if (day < 1 || day > 31) {
    				throw new ManagerBeanVetoListenerException("Revise los dias de pago.");    				
    			}
    		}
    	}
	}

	private void ensureParams(RegistryPayMethod to) {
    	if (to.getNumberOfPayments() == 0) {
    		to.setNumberOfPayments(1);
    	}
    	if (to.getPaymentDays() == null) {
    		to.setPaymentDays(StringUtils.EMPTY);
    	}
    }
}
