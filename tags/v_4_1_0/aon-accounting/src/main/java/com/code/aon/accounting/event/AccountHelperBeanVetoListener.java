package com.code.aon.accounting.event;

import com.code.aon.accounting.AccountEntryDetail;
import com.code.aon.accounting.util.AccountHelperManager;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
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
	public void vetoableBeanUpdated(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		try {
			AccountEntryDetail detail = (AccountEntryDetail) evt.getTo();
			IManagerBean detailBean = BeanManager.getManagerBean(AccountEntryDetail.class);
			AccountEntryDetail detailBD = (AccountEntryDetail) detailBean.get(detail.getId());   
			getManager().subtractOccurrence(detailBD.getAccount(), detailBD.getBalancingAccount());
		} catch (ManagerBeanException e) {
			throw new ManagerBeanVetoListenerException(e);
		}
	}

}
