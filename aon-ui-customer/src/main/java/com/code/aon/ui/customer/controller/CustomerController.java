package com.code.aon.ui.customer.controller;

import com.code.aon.customer.Customer;
import com.code.aon.person.Person;
import com.code.aon.registry.enumeration.RegistryType;
import com.code.aon.ui.form.BasicController;

/**
 * Controller used in the customer maintenance.
 */
public class CustomerController extends BasicController {
	
	private Person person;
	
	private boolean newPerson;
	
	private boolean showFinanceRegistryBank = true;
	
	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public boolean isNewPerson() {
		return newPerson;
	}

	public void setNewPerson(boolean newPerson) {
		this.newPerson = newPerson;
	}

    public boolean isNaturalType(){
    	Customer customer = (Customer)getTo();
    	return customer.getRegistry().getType().equals(RegistryType.NATURAL);
    }
    
	public boolean isShowFinanceRegistryBank() {
		return showFinanceRegistryBank;
	}

	public void setShowFinanceRegistryBank(boolean showFinanceRegistryBank) {
		this.showFinanceRegistryBank = showFinanceRegistryBank;
	}
    
}