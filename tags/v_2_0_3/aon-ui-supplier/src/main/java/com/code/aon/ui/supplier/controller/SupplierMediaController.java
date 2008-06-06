package com.code.aon.ui.supplier.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.util.AonUtil;

/**
 * Controller used in the media maintenance of the supplier.
 */
public class SupplierMediaController extends BasicController {
	
	/** Address Controller name. */
	private final static String ADDRESS_CONTROLLER_NAME = "supplierAddress";
	
    /**
     * On reset. Sends a cancel to the address controller to avoid having both controllers editing
     * 
     * @param event the event
     */
    @Override
    public void onReset(ActionEvent event) {
        IController addressController = AonUtil.getController(ADDRESS_CONTROLLER_NAME);
        addressController.onCancel(event);

        super.onReset(event);
    }

    /**
     * On select. Sends a cancel to the address controller to avoid having both controllers editing
     * 
     * @param event the event
     */
    @Override
    public void onSelect(ActionEvent event) {
    	IController addressController = AonUtil.getController(ADDRESS_CONTROLLER_NAME);
        addressController.onCancel(event);

        super.onSelect(event);
    }
}