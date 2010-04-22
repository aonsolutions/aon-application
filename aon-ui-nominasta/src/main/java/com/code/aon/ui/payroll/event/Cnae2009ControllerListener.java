package com.code.aon.ui.payroll.event;



import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class Cnae2009ControllerListener extends ControllerAdapter {

	@SuppressWarnings("unchecked")
	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		
		
	}

	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		beforeBeanAdded(event);
	}
	
}