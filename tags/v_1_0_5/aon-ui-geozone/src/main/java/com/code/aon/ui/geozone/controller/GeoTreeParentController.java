package com.code.aon.ui.geozone.controller;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.ActionEvent;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.menu.jsf.MenuEvent;

public class GeoTreeParentController extends BasicController {
	
	private static final Logger LOGGER = Logger.getLogger(GeoTreeParentController.class.getName());

	@SuppressWarnings("unused")
    public void onSearch(MenuEvent event) {
    	try {
    		clearCriteria();
    	} catch(ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error clearing criteria searching countries", e);
    	}
        this.onSearch((ActionEvent)event);
    }

}