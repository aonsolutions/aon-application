package com.code.aon.ui.customer.event;

import static com.code.aon.ui.customer.controller.ICustomerConstants.CUSTOMER_CONTROLLER_NAME;
import static com.code.aon.ui.customer.controller.ICustomerConstants.SHOW_PERSON;

import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.person.Person;
import com.code.aon.registry.Registry;
import com.code.aon.registry.enumeration.DocumentType;
import com.code.aon.registry.enumeration.RegistryType;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;

public class CustomerPersonListener extends ControllerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private final static Logger LOGGER = LoggerFactory.getLogger(CustomerPersonListener.class);
	
	private Person person;
	
	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}
	
	private void resetPerson() throws ManagerBeanException {
		Person person = (Person) BeanManager.getManagerBean(Person.class).createNewTo();
		setPerson( person );
	}
	
	private boolean isEnabled() {
		return AonUtil.isBeanValue(CUSTOMER_CONTROLLER_NAME, SHOW_PERSON);
	}

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		if ( isEnabled() ) {
			try {
				resetPerson();
				Registry registry = ((Customer) event.getController().getTo()).getRegistry();
				registry.setType(RegistryType.NATURAL);
				registry.setDocumentType(DocumentType.NIF);
			} catch (ManagerBeanException e) {
				LOGGER.error(e.getMessage(), e);
				throw new ControllerListenerException(e.getMessage(), e);
			}			
		}
	}
	
	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		if ( isEnabled() ) {
			try {
				resetPerson();
				Customer customer = (Customer) event.getController().getTo();
				IManagerBean personBean = BeanManager.getManagerBean(Person.class);
				Person person = (Person) personBean.get(customer.getRegistry().getId());
				if ( person != null ) {
					setPerson(person);	
				}
			} catch (ManagerBeanException e) {
				LOGGER.error(e.getMessage(), e);
				throw new ControllerListenerException(e.getMessage(), e);
			}			
		}
	}

	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		createOrUpdatePerson(event);		
	}
	
	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		createOrUpdatePerson(event);
	}
		
	private void createOrUpdatePerson(ControllerEvent event) throws ControllerListenerException {
		if ( isEnabled() ) {		
			try {
				Registry registry = ((Customer) event.getController().getTo()).getRegistry();
				if (registry.getType().equals(RegistryType.NATURAL)) {
					boolean update = getPerson().getId() != null;
					getPerson().setRegistry(registry);
					getPerson().setId(registry.getId());
					getPerson().setName(registry.getName());
					IManagerBean personBean = BeanManager.getManagerBean(Person.class);
					if ( update ) {
						personBean.update(getPerson());						
					} else {
						personBean.insert(getPerson());	
					}
				}
			} catch (ManagerBeanException e) {
				LOGGER.error(e.getMessage(), e);
				throw new ControllerListenerException(e.getMessage(), e);
			}
		}
	}

	public int getYears() {
		Calendar dob = Calendar.getInstance();
		dob.setTime(getPerson().getBirthDate());
		Calendar today = Calendar.getInstance();
		int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
		if (today.get(Calendar.DAY_OF_YEAR) <= dob.get(Calendar.DAY_OF_YEAR)) {
			age--;	
		}
		LOGGER.info( "Birth Day: {}, age: {}", getPerson().getBirthDate(), age );
		return age;		
	}
	
}
