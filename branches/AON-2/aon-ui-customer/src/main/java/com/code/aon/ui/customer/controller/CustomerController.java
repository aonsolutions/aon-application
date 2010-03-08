package com.code.aon.ui.customer.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.customer.Customer;
import com.code.aon.person.Person;
import com.code.aon.registry.enumeration.RegistryType;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.menu.jsf.MenuEvent;
import com.code.aon.ui.util.AonUtil;

/**
 * Controller used in the customer maintenance.
 */
public class CustomerController extends BasicController {
	
	private static final String CUSTOMER_ADDRESS_CONTROLLER_NAME = "customerAddress";
	
	private static final String CUSTOMER_MEDIA_CONTROLLER_NAME = "customerMedia";
	
	private Person person;
	
	private boolean newPerson;
	
	
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

	/**
     * On reset. Method launched by the menu
     * 
     * @param event the event
     */
    @SuppressWarnings("unused")
    public void onReset(MenuEvent event) {
        this.onReset((ActionEvent)event);
    }

	/**
     * On editSearch. Method launched by the menu
     * 
     * @param event the event
     */
    @SuppressWarnings("unused")
    public void onEditSearch(MenuEvent event) {
        this.onEditSearch((ActionEvent)event);
    }

    public boolean isNaturalType(){
    	Customer customer = (Customer)getTo();
    	return customer.getRegistry().getType().equals(RegistryType.NATURAL);
    }
    
    /**
     * On reset. Sends a cancel to the media and address controllers to avoid having editing any of them
     * 
     * @param event the event
     * 
     * @see com.code.aon.ui.form.BasicController#onReset(javax.faces.event.ActionEvent)
     */
    @Override
    public void onReset(ActionEvent event) {
    	IController addressController = AonUtil.getController(CUSTOMER_ADDRESS_CONTROLLER_NAME);
    	if(addressController != null){
    		addressController.onCancel(event);
    	}

        IController mediaController = AonUtil.getController(CUSTOMER_MEDIA_CONTROLLER_NAME);
        if(mediaController != null){
        	mediaController.onCancel(event);
        }

        super.onReset(event);
    }

    /**
     * On select. Sends a cancel to the media and address controllers to avoid having editing any of them
     * 
     * @param event the event
     * 
     * @see com.code.aon.ui.form.BasicController#onSelect(javax.faces.event.ActionEvent)
     */
    @Override
    public void onSelect(ActionEvent event) {
    	IController addressController = AonUtil.getController(CUSTOMER_ADDRESS_CONTROLLER_NAME);
    	if(addressController != null){
    		addressController.onCancel(event);
    	}

        IController mediaController = AonUtil.getController(CUSTOMER_MEDIA_CONTROLLER_NAME);
        if(mediaController != null){
        	mediaController.onCancel(event);
        }

        super.onSelect(event);
    }
}