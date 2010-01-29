package com.code.aon.purchase.event;

import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.purchase.Purchase;

public class PurchaseBeanVetoListener extends ManagerBeanVetoListenerAdapter {

	@Override
	public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		Purchase purchase = (Purchase) evt.getTo();
		if (purchase.getScope() == null || purchase.getScope().getId() == null) {
			purchase.setScope(purchase.getSupplier().getScope());
		}
	}

}