package com.code.aon.ui.fiscal.controller.model;

import com.code.aon.common.AonException;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class FiscalModelControllerListener extends ControllerAdapter {

	private FiscalModelController getController(ControllerEvent event) {
		FiscalModelController c = (FiscalModelController) event.getController();
		return c;
	}
	
	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		try {
			FiscalModelController c = getController(event);	
			c.load();
			c.setFileOutput(null);
		} catch (AonException e) {
			throw new ControllerListenerException(e.getMessage(),e);
		}
	}
	
	@Override
	public void afterBeanCanceled(ControllerEvent event) throws ControllerListenerException {
		try {
			FiscalModelController c = getController(event);	
			c.unload();
			c.setFileOutput(null);
		} catch (AonException e) {
			throw new ControllerListenerException(e.getMessage(),e);
		}
	}

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		try {
			FiscalModelController c = getController(event);	
			c.initialize();
			c.setFileOutput(null);
		} catch (AonException e) {
			throw new ControllerListenerException(e.getMessage(),e);
		}
	}
	
	@Override
	public void afterBeanRemoved(ControllerEvent event) throws ControllerListenerException {
		try {
			FiscalModelController c = getController(event);	
			c.unload();
			c.setFileOutput(null);
		} catch (AonException e) {
			throw new ControllerListenerException(e.getMessage(),e);
		}
	}

	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		try {
			FiscalModelController c = getController(event);	
			c.initializeDetails();
			c.setFileOutput(null);
		} catch (AonException e) {
			throw new ControllerListenerException(e.getMessage(),e);
		}
	}
	
	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		try {
			FiscalModelController c = getController(event);	
			c.insertOrUpdateDetails();
			c.setFileOutput(null);
		} catch (AonException e) {
			throw new ControllerListenerException(e.getMessage(),e);
		}
	}
	
	@Override
	public void beforeBeanRemoved(ControllerEvent event) throws ControllerListenerException {
		try {
			getController(event).removeDetails();
		} catch (AonException e) {
			throw new ControllerListenerException(e.getMessage(),e);
		}
	}
	
}
