package com.code.aon.customer.event;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.customer.Customer;
import com.code.aon.registry.Registry;

public class CustomerBeanListener extends ManagerBeanVetoListenerAdapter {

	@Override
	public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		try {
			Customer customer = (Customer)evt.getTo();
			if(customer.getRegistry().getDocument().equals("")){
				customer.setRegistry(setRegistryDocumentToNull(customer.getRegistry()));
			}
		} catch (ManagerBeanException e) {
			throw new ManagerBeanVetoListenerException(e);
		}
	}

	@Override
	public void vetoableBeanUpdated(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		try {
			Customer customer = (Customer)evt.getTo();
			if(customer.getRegistry().getDocument().equals("")){
				customer.setRegistry(setRegistryDocumentToNull(customer.getRegistry()));
			}
		} catch (ManagerBeanException e) {
			throw new ManagerBeanVetoListenerException(e);
		}
	}
	
	private Registry setRegistryDocumentToNull(Registry registry) throws ManagerBeanException {
		IManagerBean registryBean = BeanManager.getManagerBean(Registry.class);
		registry.setDocument(null);
		return (Registry) registryBean.update(registry);
	}
}