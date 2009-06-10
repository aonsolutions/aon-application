package com.code.aon.accounting.event;

import com.code.aon.accounting.Balance;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;

/**
 * @author Consulting & Development
 *
 */
public class BalanceBeanVetoListener extends ManagerBeanVetoListenerAdapter {

	@Override
    public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		Balance balance = (Balance) evt.getTo();
		balance.setRemovable(true);
		balance.setType(null);
    }
	

}
