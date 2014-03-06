package com.code.aon.common.domain;

import com.code.aon.common.AonVersion;
import com.code.aon.common.event.FinderBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.ql.Criteria;

public class DomainBeanVetoListener extends ManagerBeanVetoListenerAdapter {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	@Override
	public void vetoableBeanSearched(FinderBeanEvent evt) throws ManagerBeanVetoListenerException {
		Criteria criteria = evt.getCriteria();
		if (! criteria.isSkipDomainFilter() ) {
			criteria.addExpression(DomainManager.getCurrentDomainExpression(evt.getEntityClass()));
			criteria.setSkipDomainFilter(true);
		}
	}

}