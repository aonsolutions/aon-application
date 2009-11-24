package com.code.aon.warehouse.event;

import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.warehouse.Income;
import com.code.aon.warehouse.enumeration.IncomeStatus;

public class IncomeBeanVetoListener extends ManagerBeanVetoListenerAdapter {

	@Override
	public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		Income income = (Income) evt.getTo();
		setDefaultValues(income);
	}

	private void setDefaultValues(Income income) {
		if (income.getSecurityLevel() == null) {
			income.setSecurityLevel(SecurityLevel.OFFICIAL);
		}
		if (income.getStatus() == null) {
			income.setStatus(IncomeStatus.PENDING);
		}
		if (income.getScope() == null || income.getScope().getId() == null) {
			income.setScope(income.getSupplier().getScope());
		}
	}

}