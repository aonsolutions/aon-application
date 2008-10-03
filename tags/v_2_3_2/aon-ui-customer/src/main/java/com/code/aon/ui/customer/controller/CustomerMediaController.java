package com.code.aon.ui.customer.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.util.AonUtil;


/**
 * Controller used in the media maintenance of the customer.
 */
public class CustomerMediaController extends BasicController {
	
	/** CustomerAddress Controller name. */
	private static final String CUSTOMER_ADDRESS_CONTROLLER_NAME = "customerAddress";
	
    /**
     * On reset. Sends a cancel to the address controller to avoid having both controllers editing
     * 
     * @param event the event
     */
    @Override
    public void onReset(ActionEvent event) {
    	IController addressController = AonUtil.getController(CUSTOMER_ADDRESS_CONTROLLER_NAME);
    	if(addressController != null){
    		addressController.onCancel(event);
    	}

        super.onReset(event);
    }

    /**
     * On select. Sends a cancel to the address controller to avoid having both controllers editing
     * 
     * @param event the event
     */
    @Override
    public void onSelect(ActionEvent event) {
    	IController addressController = AonUtil.getController(CUSTOMER_ADDRESS_CONTROLLER_NAME);
    	if(addressController != null){
    		addressController.onCancel(event);
    	}

        super.onSelect(event);
    }
}