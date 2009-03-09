package com.code.aon.desktop.event;

import java.util.logging.Logger;

import com.code.aon.desktop.Domain;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class DomainControllerListener extends ControllerAdapter {

	private static final Logger LOGGER = Logger.getLogger(DomainControllerListener.class.getName());

	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		Domain domain = (Domain) event.getController().getTo();
		domain.setUserManagement(true);
	}
	
}
