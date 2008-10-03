package com.code.aon.ui.customer.event;

import java.util.Iterator;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.person.Person;
import com.code.aon.person.dao.IPersonAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.Registry;
import com.code.aon.registry.enumeration.RegistryType;
import com.code.aon.ui.customer.controller.CustomerController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class CustomerControllerPersonListener extends ControllerAdapter {

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		CustomerController customerController = (CustomerController)event.getController();
		Person person = new Person();
		person.setRegistry(new Registry());
		customerController.setPerson(person);
		customerController.setNewPerson(true);
		((Customer)customerController.getTo()).getRegistry().setType(RegistryType.NATURAL);
	}
	
	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		CustomerController customerController = (CustomerController)event.getController();
		Customer customer = (Customer)customerController.getTo();
		if(customer.getRegistry().getType().equals(RegistryType.NATURAL)){
			Person person = customerController.getPerson();
			Registry registry = ((Customer)customerController.getTo()).getRegistry();
			person.setRegistry(registry);
			person.setId(registry.getId());
			createOrUpdatePerson(person, customerController.isNewPerson());
		}
		customerController.setNewPerson(false);
	}
	
	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		CustomerController customerController = (CustomerController)event.getController();
		Customer customer = (Customer)customerController.getTo();
		if(customer.getRegistry().getType().equals(RegistryType.NATURAL)){
			Person person = customerController.getPerson();
			Registry registry = ((Customer)customerController.getTo()).getRegistry();
			person.setRegistry(registry);
			person.setId(registry.getId());
			createOrUpdatePerson(person, customerController.isNewPerson());
		}
		customerController.setNewPerson(false);
	}
	
	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		CustomerController customerController = (CustomerController)event.getController();
		customerController.setPerson(obtainPerson(((Customer)customerController.getTo()).getRegistry()));
		customerController.setNewPerson(customerController.getPerson().getId() == null);
	}
	
	private void createOrUpdatePerson(Person person, boolean newCustomer) throws ControllerListenerException {
		try {
			IManagerBean personBean = BeanManager.getManagerBean(Person.class);
			if(newCustomer){
				personBean.insert(person);
			}else{
				personBean.update(person);
			}
		} catch (ManagerBeanException e) {
			e.printStackTrace();
			throw new ControllerListenerException("Error creating or updating Person with id=" + person.getRegistry().getId(),e);
		}
	}
	
	@SuppressWarnings("unchecked")
	private Person obtainPerson(Registry registry) throws ControllerListenerException {
		try {
			IManagerBean personBean = BeanManager.getManagerBean(Person.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(personBean.getFieldName(IPersonAlias.PERSON_REGISTRY_ID), registry.getId());
			Iterator iter = personBean.getList(criteria).iterator();
			if(iter.hasNext()){
				return (Person)iter.next();
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException("Error obtaining person related with registry with id=" + registry.getId());
		}
		Person person = new Person();
		person.setRegistry(registry);
		return person;
	}
}