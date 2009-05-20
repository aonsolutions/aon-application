package com.code.aon.account.event;

import com.code.aon.account.Account;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;

public class AccountBeanVetoListener extends ManagerBeanVetoListenerAdapter {

    @Override
    public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
    	Account to = (Account)evt.getTo();
    	to.setEntryEnabled(true);

    	int level = to.getId().length();
    	if (level == 5) {
    		level = 4;
    	}
    	if (level > 5) {
    		level = 5;
    	}
    	to.setLevel(level);
    }

}
