package com.code.aon.finance.event;

import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.finance.FinanceBatch;

public class FinanceBatchBeanVetoListener extends ManagerBeanVetoListenerAdapter {

	@Override
	public void vetoableBeanRemoved(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		FinanceBatch fBatch = (FinanceBatch)evt.getTo();
		if (fBatch.getSecurityLevel() == null) {
			fBatch.setSecurityLevel(SecurityLevel.OFFICIAL);
		}
	}

}