package com.code.aon.ui.registry.controller.event;

import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.registry.controller.PersonController;

/**
 * Listener added to the CompanyController
 * 
 */
public class PersonControllerListener extends ControllerAdapter {
	
	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		PersonController c = (PersonController)event.getController();
		c.initDocument();
	}

}
