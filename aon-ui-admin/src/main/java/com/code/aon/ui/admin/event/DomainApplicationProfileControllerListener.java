package com.code.aon.ui.admin.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.ui.admin.controller.ApplicationProfileController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class DomainApplicationProfileControllerListener extends ControllerAdapter {

	private final static Logger LOGGER = LoggerFactory.getLogger(DomainApplicationProfileControllerListener.class);

	@Override
	public void afterBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		ApplicationProfileController apc = (ApplicationProfileController) event.getController();
		try {		
			apc.initProfileInfos();
		} catch (Throwable e) {
			LOGGER.error(e.getMessage(), e);
			throw new ControllerListenerException( e.getMessage(), e );
		}		
	}
	
	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		ApplicationProfileController apc = (ApplicationProfileController) event.getController();
		try {		
			apc.initProfileInfos();
		} catch (Throwable e) {
			LOGGER.error(e.getMessage(), e);
			throw new ControllerListenerException( e.getMessage(), e );
		}		
	}

	@Override
	public void afterBeanUpdated(ControllerEvent event)
			throws ControllerListenerException {
		ApplicationProfileController apc = (ApplicationProfileController) event.getController();
		try {		
			apc.onSaveProfile();
		} catch (Throwable e) {
			LOGGER.error(e.getMessage(), e);
			throw new ControllerListenerException( e.getMessage(), e );
		}		
	}	
}
