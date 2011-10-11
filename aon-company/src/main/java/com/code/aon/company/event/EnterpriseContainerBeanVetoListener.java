package com.code.aon.company.event;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.company.IEnterprise;
import com.code.aon.company.util.CompanyUtil;

public class EnterpriseContainerBeanVetoListener extends ManagerBeanVetoListenerAdapter {

	@Override
	public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		try {
			CompanyUtil companyUtil = new CompanyUtil();
			IEnterprise container =  (IEnterprise) evt.getTo();
			container.setEnterprise(companyUtil.getActiveEnterprise());
		} catch (ManagerBeanException e) {
			throw new ManagerBeanVetoListenerException(e.getMessage(),e);
		}
	}
	
}
