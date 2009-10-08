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
	
	private String selectedTab;
	
	private boolean showFinanceData = true;
	private boolean showProjectData = false;
	private boolean showFinanceFeeData = false;
	private boolean showAccount = false;
	private boolean showTariff  = true;
	private boolean showSegment  = true;
	
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

	public boolean isShowFinanceData() {
		return showFinanceData;
	}

	public void setShowFinanceData(boolean showFinanceData) {
		this.showFinanceData = showFinanceData;
	}
        
	public boolean isShowProjectData() {
		return showProjectData;
	}

	public void setShowProjectData(boolean showProjectData) {
		this.showProjectData = showProjectData;
	}

	public boolean isShowFinanceFeeData() {
		return showFinanceFeeData;
	}

	public void setShowFinanceFeeData(boolean showFinanceFeeData) {
		this.showFinanceFeeData = showFinanceFeeData;
	}

	public String getSelectedTab() {
		return selectedTab;
	}

	public void setSelectedTab(String selectedTab) {
		this.selectedTab = selectedTab;
	}

	public boolean isShowAccount() {
		return showAccount;
	}

	public void setShowAccount(boolean showAccount) {
		this.showAccount = showAccount;
	}
	
	public boolean isShowTariff() {
		return showTariff;
	}

	public void setShowTariff(boolean showTariff) {
		this.showTariff = showTariff;
	}

	public boolean isShowSegment() {
		return showSegment;
	}

	public void setShowSegment(boolean showSegment) {
		this.showSegment= showSegment;
	}
	
}