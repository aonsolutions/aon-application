package com.esferalia.aon.entity.event;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.domain.IDomain;
import com.code.aon.common.event.FinderBeanEvent;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.config.Domain;

public class DomainBeanVetoListener extends ManagerBeanVetoListenerAdapter {

	private static final String DOMAIN_ID_PROPERTY = ".domain.id";
	private Domain currentDomain;

	@SuppressWarnings("unchecked")
	@Override
	public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		IDomain<Domain> iDomain =  (IDomain<Domain>) evt.getTo();
		if (iDomain.getDomain() == null || iDomain.getDomain().getId() == null) {
			iDomain.setDomain(getCurrentDomain());
		}
	}

	@Override
	public void vetoableBeanSearched(FinderBeanEvent evt) throws ManagerBeanVetoListenerException {
		String className  = evt.getEntityClass().getSimpleName();
		String alias = className + DOMAIN_ID_PROPERTY;
		evt.getCriteria().addEqualExpression(alias, getCurrentDomain().getId());
	}

	/* TODO Implementar correctamente esta funcionalidad */
	private Domain getCurrentDomain() {
		if (currentDomain == null) {
			try {
				IManagerBean bean = BeanManager.getManagerBean(Domain.class);
				currentDomain = (Domain) bean.get(1);
			} catch (Throwable e) {
				return null;
			}
		}
		return currentDomain;
	}

}