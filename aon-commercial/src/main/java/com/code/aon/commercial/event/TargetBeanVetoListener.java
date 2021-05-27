package com.code.aon.commercial.event;

import com.code.aon.commercial.Target;
import com.code.aon.commercial.enumeration.TargetStatus;
import com.code.aon.AonVersion;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.config.enumeration.InvoiceTransactionType;

public class TargetBeanVetoListener extends ManagerBeanVetoListenerAdapter {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
    @Override
    public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
    	Target to = (Target)evt.getTo();
    	if (to.getStatus() == null) {
    		to.setStatus(TargetStatus.ACTIVE);
    	}
    	if (to.getTransaction() == null) {
    		to.setTransaction(InvoiceTransactionType.NATIONAL);
    	}
    }

}
