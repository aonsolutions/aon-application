package com.code.aon.account.event;

import com.code.aon.account.Account;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;

public class AccountBeanVetoListener extends ManagerBeanVetoListenerAdapter {

    @Override
    public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
    	Account to = (Account)evt.getTo();
    	if(!checkValidLength(to)){
			throw new ManagerBeanVetoListenerException("Valid id lengths are: 1, 2, 3, 5 or 12");
		}
    	to.setEntryEnabled(true);
    	int level = to.getId().length();
    	if (level == 5 ) {
    		level = 4;
    	}
    	if (level >5 ) {
    		level = 5;
    	}
    	to.setLevel(level);
    }
	
    @Override
    public void vetoableBeanUpdated(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
    	Account to = (Account)evt.getTo();
    	if(!checkValidLength(to)){
    		throw new ManagerBeanVetoListenerException("Valid id lengths are: 1, 2, 3, 5 or 12");
		}
	}

	private boolean checkValidLength(Account account) {
		int l = account.getId().length();
		if (l == 1 || l == 2 || l == 3 || l == 5 || l == 12 ){
			return true;
		}
		return false;
	}
    
}
