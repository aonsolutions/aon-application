package com.code.aon.account.event;

import com.code.aon.account.Account;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;

public class AccountBeanVetoListener extends ManagerBeanVetoListenerAdapter {

	private static final String ERROR_MSG = "La cuenta debe tener una longitud de 1, 2, 3, 4 ó 9 caracteres.";
	
    @Override
    public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
    	Account to = (Account)evt.getTo();
    	if (!checkValidLength(to)) {
			throw new ManagerBeanVetoListenerException(ERROR_MSG);
		}

    	int level = (to.getId().length() > 4) ? 5 : to.getId().length();
    	to.setLevel(level);
    	to.setEntryEnabled(level==5);
    }
	
    @Override
    public void vetoableBeanUpdated(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
    	Account to = (Account)evt.getTo();
    	if (!checkValidLength(to)) {
			throw new ManagerBeanVetoListenerException(ERROR_MSG);
		}
	}

	private boolean checkValidLength(Account account) {
		int length = account.getId().length();
		if (length == 1 || length == 2 || length == 3 || length == 4 || length == 9 ) {
			return true;
		}
		return false;
	}

}
