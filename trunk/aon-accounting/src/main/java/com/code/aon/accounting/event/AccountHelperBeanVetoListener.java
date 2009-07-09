package com.code.aon.accounting.event;

import com.code.aon.accounting.AccountEntryDetail;
import com.code.aon.accounting.util.AccountHelperManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;

/**
 * @author Consulting & Development
 * 
 */
public class AccountHelperBeanVetoListener extends ManagerBeanVetoListenerAdapter {

	private AccountHelperManager manager;

	private AccountHelperManager getManager() {
		if (manager == null) {
			manager = new AccountHelperManager();
		}
		return manager;
	}

	@Override
	public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		try {
			AccountEntryDetail detail = (AccountEntryDetail) evt.getTo();
			getManager().addOccurrence(detail.getAccount(), detail.getBalancingAccount());
		} catch (ManagerBeanException e) {
			throw new ManagerBeanVetoListenerException(e);
		}
	}

	@Override
	public void vetoableBeanUpdated(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		try {
			AccountEntryDetail detail = (AccountEntryDetail) evt.getTo();
			getManager().addOccurrence(detail.getAccount(), detail.getBalancingAccount());
		} catch (ManagerBeanException e) {
			throw new ManagerBeanVetoListenerException(e);
		}
	}

	@Override
	public void vetoableBeanRemoved(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		try {
			AccountEntryDetail detail = (AccountEntryDetail) evt.getTo();
			getManager().subtractOccurrence(detail.getAccount(), detail.getBalancingAccount());
		} catch (ManagerBeanException e) {
			throw new ManagerBeanVetoListenerException(e);
		}
	}

}
