package com.code.aon.company.event;

import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.company.Company;

public class CompanyDomainBeanVetoListener extends ManagerBeanVetoListenerAdapter {

	@Override
	public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		Company company =  (Company) evt.getTo();
		if (company.getDomain() == 0) {
			company.setDomain( DomainManager.getCurrentDomain() );
		}
	}

}
