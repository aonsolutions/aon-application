package com.code.aon.accounting.event;

import com.code.aon.accounting.AccountEntryDetail;
import com.code.aon.accounting.util.AccountHelperManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanListenerAdapter;

/**
 * @author Consulting & Development
 * 
 */
public class AccountHelperBeanListener extends ManagerBeanListenerAdapter {

	private AccountHelperManager manager;

	private AccountHelperManager getManager() {
		if (manager == null) {
			manager = new AccountHelperManager();
		}
		return manager;
	}

	@Override
	public void beanInserted(ManagerBeanEvent evt) throws ManagerBeanException {
		AccountEntryDetail detail = (AccountEntryDetail) evt.getTo();
		getManager().addOccurrence(detail.getAccount(), detail.getBalancingAccount());
	}

	@Override
	public void beanUpdated(ManagerBeanEvent evt) throws ManagerBeanException {
		AccountEntryDetail detail = (AccountEntryDetail) evt.getTo();
		getManager().addOccurrence(detail.getAccount(), detail.getBalancingAccount());
	}

	@Override
	public void beanRemoved(ManagerBeanEvent evt) throws ManagerBeanException {
		AccountEntryDetail detail = (AccountEntryDetail) evt.getTo();
		getManager().subtractOccurrence(detail.getAccount(), detail.getBalancingAccount());
	}

}
