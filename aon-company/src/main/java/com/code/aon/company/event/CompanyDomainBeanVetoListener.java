package com.code.aon.company.event;

import com.code.aon.common.AonVersion;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.company.Company;

public class CompanyDomainBeanVetoListener extends ManagerBeanVetoListenerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Override
	public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		Company company =  (Company) evt.getTo();
		if (company.getDomain() == 0) {
			company.setDomain( DomainManager.getCurrentDomain() );
		}
	}

}
