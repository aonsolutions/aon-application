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
    }
	
	@Override
    public void vetoableBeanRemoved(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		Balance balance = (Balance) evt.getTo();
		if (!balance.isRemovable()) {
			throw new ManagerBeanVetoListenerException("El balance no se puede borrar. Está configurado en el sistema.");
		}
    }

}
