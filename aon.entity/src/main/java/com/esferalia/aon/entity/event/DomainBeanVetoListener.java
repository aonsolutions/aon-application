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

	private static final String DOMAIN_ID_PROPERTY = ".domain.id";

	@SuppressWarnings("unchecked")
	@Override
	public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		IDomain<Domain> iDomain =  (IDomain<Domain>) evt.getTo();
		System.out.println("DOMAIN LISTENER ..:" + iDomain.getClass().getName());
		if (iDomain.getDomain() == null || iDomain.getDomain().getId() == null) {
			System.out.println("DOMAIN LISTENER ..(domain)..: " + getCurrentDomain().getId());
			iDomain.setDomain(getCurrentDomain());
		}
		if (!Registry.class.equals(iDomain.getClass()) 
			&& IRegistry.class.isAssignableFrom(iDomain.getClass())) {
			System.out.println("DOMAIN LISTENER ..(registry)..: " + getCurrentDomain().getId());
			Registry registry = ((IRegistry) iDomain).getRegistry();
			if (registry.getDomain() == null || registry.getDomain().getId() == null) {
				registry.setDomain(getCurrentDomain());
			}
		}
	}

	@Override
	public void vetoableBeanSearched(FinderBeanEvent evt) throws ManagerBeanVetoListenerException {
		String className  = evt.getEntityClass().getSimpleName();
		String alias = className + DOMAIN_ID_PROPERTY;
		evt.getCriteria().addEqualExpression(alias, getCurrentDomain().getId());
	}

	/* TODO Implementar correctamente esta funcionalidad */
	private Domain getCurrentDomain() throws ManagerBeanVetoListenerException {
		try {
			IManagerBean bean = BeanManager.getManagerBean(Domain.class);
			return (Domain) bean.getList(null).get(0);
		} catch (ManagerBeanException e) {
			throw new ManagerBeanVetoListenerException("No existe Domain.");
		}
	}

}