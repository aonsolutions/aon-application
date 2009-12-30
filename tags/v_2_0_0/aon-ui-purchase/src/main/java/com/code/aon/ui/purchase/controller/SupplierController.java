package com.code.aon.ui.purchase.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.menu.jsf.MenuEvent;
import com.code.aon.ui.util.AonUtil;

/**
 * Controller used in the supplier maintenance.
 */
public class SupplierController extends BasicController {
	
	/** Addres Controller name. */
	private static final String ADDRESS_CONTROLLER_NAME = "supplierAddress";
	
	/** Media Controller name. */
	private static final String MEDIA_CONTROLLER_NAME = "supplierMedia";
	
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
     * On reset. Sends a cancel to the media and address controllers to avoid having editing any of them
     * 
     * @param event the event
     * 
     * @see com.code.aon.ui.form.BasicController#onReset(javax.faces.event.ActionEvent)
     */
    @Override
    public void onReset(ActionEvent event) {
    	IController addressController = AonUtil.getController(ADDRESS_CONTROLLER_NAME);
        addressController.onCancel(event);

        IController mediaController = AonUtil.getController(MEDIA_CONTROLLER_NAME);
        mediaController.onCancel(event);

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
    	IController addressController = AonUtil.getController(ADDRESS_CONTROLLER_NAME);
        addressController.onCancel(event);

        IController mediaController = AonUtil.getController(MEDIA_CONTROLLER_NAME);
        mediaController.onCancel(event);

        super.onSelect(event);
    }
}