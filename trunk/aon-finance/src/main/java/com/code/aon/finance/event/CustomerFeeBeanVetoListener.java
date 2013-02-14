package com.code.aon.finance.event;

import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.finance.CustomerFee;

public class CustomerFeeBeanVetoListener extends ManagerBeanVetoListenerAdapter {

    @Override
    public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
    	CustomerFee to = (CustomerFee)evt.getTo();
    	if (to.getSecurityLevel() == null) {
    		to.setSecurityLevel(SecurityLevel.OFFICIAL);
    	}
    }

}
