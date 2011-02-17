package com.code.aon.registry.event;

import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.person.Person;

public class PersonBeanVetoListener extends ManagerBeanVetoListenerAdapter {

    @Override
    public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
    	Person person = (Person) evt.getTo();
    	mergeRegistry( person );
    }

    @Override
    public void vetoableBeanUpdated(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
    	Person person = (Person) evt.getTo();
    	mergeRegistry( person );
    }

	private void mergeRegistry(Person person) {
		person.getRegistry().setName(person.getFullName());
	}

}
