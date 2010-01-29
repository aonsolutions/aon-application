package com.code.aon.sales.event;

import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.sales.Sales;

public class SalesBeanVetoListener extends ManagerBeanVetoListenerAdapter {

	@Override
	public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		Sales sales = (Sales) evt.getTo();
		if (sales.getScope() == null || sales.getScope().getId() == null) {
			sales.setScope(sales.getCustomer().getScope());
		}
	}

}