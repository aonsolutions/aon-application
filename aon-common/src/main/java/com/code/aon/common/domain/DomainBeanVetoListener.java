package com.code.aon.common.domain;

import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.event.FinderBeanEvent;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;

public class DomainBeanVetoListener extends ManagerBeanVetoListenerAdapter {

	private static final String DOMAIN_PROPERTY = ".domain";

	@Override
	public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
/*		
		IDomain iDomain =  (IDomain) evt.getTo();
		Integer currentDomain = DomainManager.getCurrentDomain();
		System.out.println("DOMAIN LISTENER ..:" + iDomain.getClass().getName());
		if (iDomain.getDomain() == 0) {
			System.out.println("DOMAIN LISTENER ..(domain)..: " + currentDomain);
			iDomain.setDomain(currentDomain);
		}
		if (!Registry.class.equals(iDomain.getClass()) 
			&& IRegistry.class.isAssignableFrom(iDomain.getClass())) {
			System.out.println("DOMAIN LISTENER ..(registry)..: " + currentDomain);
			Registry registry = ((IRegistry) iDomain).getRegistry();
			if (registry.getDomain() == 0) {
				registry.setDomain(currentDomain);
			}
		}
		if (IProject.class.isAssignableFrom(iDomain.getClass())) {
			System.out.println("DOMAIN LISTENER ..(project)..: " + currentDomain);
			Project project = ((IProject) iDomain).getProject();
			if (project.getDomain() == 0) {
				project.setDomain(currentDomain);
			}
		}
		if (IAsset.class.isAssignableFrom(iDomain.getClass())) {
			System.out.println("DOMAIN LISTENER ..(asset)..: " + currentDomain);
			Asset asset = ((IAsset) iDomain).getAsset();
			if (asset.getDomain() == 0) {
				asset.setDomain(currentDomain);
			}
		}
*/
	}

	@Override
	public void vetoableBeanSearched(FinderBeanEvent evt) throws ManagerBeanVetoListenerException {
		String className  = evt.getEntityClass().getSimpleName();
		String alias = className + DOMAIN_PROPERTY;
		evt.getCriteria().addExpression(DomainManager.getCurrentDomainExpression(alias));
	}

}