package com.code.aon.common.domain;

import com.code.aon.common.event.FinderBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.ql.Criteria;

public class DomainBeanVetoListener extends ManagerBeanVetoListenerAdapter {

	private static final String DOMAIN_PROPERTY = ".domain";

	@Override
	public void vetoableBeanSearched(FinderBeanEvent evt) throws ManagerBeanVetoListenerException {
		Criteria criteria = evt.getCriteria();
		if (! criteria.isSkipDomainFilter() ) {
			String className  = evt.getEntityClass().getSimpleName();
			String alias = className + DOMAIN_PROPERTY;
			criteria.addExpression(DomainManager.getCurrentDomainExpression(alias));
			criteria.setSkipDomainFilter(true);
		}
	}

}