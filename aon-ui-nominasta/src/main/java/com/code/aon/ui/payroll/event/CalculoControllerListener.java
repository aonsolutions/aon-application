package com.code.aon.ui.payroll.event;

import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.payroll.controller.CalculoController;
import com.code.aon.ui.payroll.controller.IPayrollConstants;

public class CalculoControllerListener extends ControllerAdapter implements IPayrollConstants {

	@Override
	public void beforeBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		System.out.println("beforeBeanAdded");
		((CalculoController)getController()).generarNumero(null);
		
	}
	
	@Override
	public void afterBeanUpdated(ControllerEvent event)
			throws ControllerListenerException {
		
		
		super.afterBeanUpdated(event);
	}

	@Override
	public void beforeBeanUpdated(ControllerEvent event)
			throws ControllerListenerException {
		System.out.println("beforeBeanUpdated");
		
	}

	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		
		super.afterBeanSelected(event);
	}
	
	@Override
	public void beforeBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
				
		super.beforeBeanSelected(event);
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

}
