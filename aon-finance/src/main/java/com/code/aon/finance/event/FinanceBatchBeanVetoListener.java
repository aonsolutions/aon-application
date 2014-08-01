package com.code.aon.finance.event;

import com.code.aon.AonVersion;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.finance.FinanceBatch;

public class FinanceBatchBeanVetoListener extends ManagerBeanVetoListenerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Override
	public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		FinanceBatch fBatch = (FinanceBatch)evt.getTo();
		if (fBatch.getBankStatementLink() != null && fBatch.getBankStatementLink().getId() == null) {
			fBatch.setBankStatementLink(null);
		}
		if (fBatch.getSecurityLevel() == null) {
			fBatch.setSecurityLevel(SecurityLevel.OFFICIAL);
		}
	}

	@Override
	public void vetoableBeanUpdated(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		FinanceBatch fBatch = (FinanceBatch)evt.getTo();
		if (fBatch.getBankStatementLink() != null && fBatch.getBankStatementLink().getId() == null) {
			fBatch.setBankStatementLink(null);
		}
		if (fBatch.getSecurityLevel() == null) {
			fBatch.setSecurityLevel(SecurityLevel.OFFICIAL);
		}
	}

}