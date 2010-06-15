package com.code.aon.ui.payroll.event;

import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.payroll.controller.IPayrollConstants;
import com.code.aon.ui.payroll.controller.PersonaController;

public class PersonaControllerListener extends ControllerAdapter implements
		IPayrollConstants {

	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		
		((PersonaController)getController()).generateCdg();
	}

	@Override
	public void beforeBeanAdded(ControllerEvent event)
			throws ControllerListenerException {

		((PersonaController)getController()).verifyNullFields();
	}
	
	@Override
	public void beforeBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		// TODO Auto-generated method stub
		System.out.println(" beforeBeanSelected ");
		
		super.beforeBeanSelected(event);
	}
}
