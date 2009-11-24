package com.code.aon.warehouse.event;

import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.warehouse.Income;

public class IncomeBeanVetoListener extends ManagerBeanVetoListenerAdapter {

	@Override
	public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		Income income = (Income) evt.getTo();
		if (income.getScope() == null || income.getScope().getId() == null) {
			income.setScope(income.getSupplier().getScope());
		}
	}

}