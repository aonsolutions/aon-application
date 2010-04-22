package com.code.aon.ui.payroll.event;

import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.payroll.controller.Linimpresos190Controller;

public class Linimpresos190ControllerListener extends ControllerAdapter {

	@Override
	public void beforeBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		System.out.println("beforeBeanAdded");
		//((Impresos11xController)getController()).generarNumero(null);
		((Linimpresos190Controller)getController()).setDefaultFields();
		
	}

	@Override
	public void beforeBeanUpdated(ControllerEvent event)
			throws ControllerListenerException {
		System.out.println("beforeBeanUpdated");
		((Linimpresos190Controller)getController()).setDefaultFields();
		
	}

	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {

	}

	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {

		super.afterBeanCreated(event);
	}

	@Override
	public void afterBeanReset(ControllerEvent event)
			throws ControllerListenerException {
		
		super.afterBeanReset(event);
	}
	
	@Override
	public void afterModelInitialized(ControllerEvent event)
			throws ControllerListenerException {
		
		super.afterModelInitialized(event);
	}

}
