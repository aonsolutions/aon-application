package com.esferalia.aon.entity.event;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.IDomain;
import com.code.aon.common.event.FinderBeanEvent;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.config.Domain;
import com.code.aon.registry.IRegistry;
import com.code.aon.registry.Registry;

public class DomainBeanVetoListener extends ManagerBeanVetoListenerAdapter {

	private static final String DOMAIN_PROPERTY = ".domain";

	@Override
	public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		IDomain iDomain =  (IDomain) evt.getTo();
		System.out.println("DOMAIN LISTENER ..:" + iDomain.getClass().getName());
		if (iDomain.getDomain() == 0) {
			System.out.println("DOMAIN LISTENER ..(domain)..: " + getCurrentDomain());
			iDomain.setDomain(getCurrentDomain());
		}
		if (!Registry.class.equals(iDomain.getClass()) 
			&& IRegistry.class.isAssignableFrom(iDomain.getClass())) {
			System.out.println("DOMAIN LISTENER ..(registry)..: " + getCurrentDomain());
			Registry registry = ((IRegistry) iDomain).getRegistry();
			if (registry.getDomain() == 0) {
				registry.setDomain(getCurrentDomain());
			}
		}
	}

	@Override
	public void vetoableBeanSearched(FinderBeanEvent evt) throws ManagerBeanVetoListenerException {
		String className  = evt.getEntityClass().getSimpleName();
		String alias = className + DOMAIN_PROPERTY;
		evt.getCriteria().addEqualExpression(alias, getCurrentDomain());
	}

	/* TODO Implementar correctamente esta funcionalidad */
	/* Esto tiene que desaparecer YA! */
	private Integer getCurrentDomain() throws ManagerBeanVetoListenerException {
		try {
			IManagerBean bean = BeanManager.getManagerBean(Domain.class);
			return ((Domain) bean.getList(null).get(0)).getId();
		} catch (ManagerBeanException e) {
			throw new ManagerBeanVetoListenerException("No existe Domain.");
		}
	}

}