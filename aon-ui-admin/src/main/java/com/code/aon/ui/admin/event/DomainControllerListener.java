package com.code.aon.ui.admin.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.ui.admin.controller.DomainController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class DomainControllerListener extends ControllerAdapter {

	private final static Logger LOGGER = LoggerFactory.getLogger(DomainControllerListener.class);

	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		DomainController dc = (DomainController) event.getController();
		try {		
			dc.updateParentDomains();
		} catch (Throwable e) {
			LOGGER.error(e.getMessage(), e);
			throw new ControllerListenerException( e.getMessage(), e );
		}		
	}

	@Override
	public void afterBeanUpdated(ControllerEvent event)
			throws ControllerListenerException {
		DomainController dc = (DomainController) event.getController();
		try {		
			dc.saveApplications();
		} catch (Throwable e) {
			LOGGER.error(e.getMessage(), e);
			throw new ControllerListenerException( e.getMessage(), e );
		}		
	}	

}
