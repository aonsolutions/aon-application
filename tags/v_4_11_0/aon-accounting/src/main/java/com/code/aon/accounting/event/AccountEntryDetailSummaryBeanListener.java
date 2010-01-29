package com.code.aon.accounting.event;

import com.code.aon.accounting.AccountEntryDetail;
import com.code.aon.accounting.util.AccountSummaryManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanListenerAdapter;

/**
 * @author Consulting & Development
 *
 */
public class AccountEntryDetailSummaryBeanListener extends ManagerBeanListenerAdapter {
	
	private AccountSummaryManager manager;
	
    private AccountSummaryManager getManager() {
    	if (manager == null) {
    		manager = new AccountSummaryManager();
    	}
		return manager;
	}

	@Override
    public void beanInserted(ManagerBeanEvent evt) throws ManagerBeanException {
        getManager().modifyAccountSummary((AccountEntryDetail)evt.getTo(), 1);
    }

    @Override
    public void beanUpdated(ManagerBeanEvent evt) throws ManagerBeanException {
    	getManager().modifyAccountSummary((AccountEntryDetail)evt.getTo(), 1);
    }

    @Override
    public void beanRemoved(ManagerBeanEvent evt) throws ManagerBeanException {
    	getManager().modifyAccountSummary((AccountEntryDetail)evt.getTo(), -1);
    }

}
