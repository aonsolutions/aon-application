package com.code.aon.common.domain;

import com.code.aon.common.event.FinderBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;

public class DomainBeanVetoListener extends ManagerBeanVetoListenerAdapter {

	private static final String DOMAIN_PROPERTY = ".domain";

	@Override
	public void vetoableBeanSearched(FinderBeanEvent evt) throws ManagerBeanVetoListenerException {
		String className  = evt.getEntityClass().getSimpleName();
		String alias = className + DOMAIN_PROPERTY;
		evt.getCriteria().addExpression(DomainManager.getCurrentDomainExpression(alias));
	}

}