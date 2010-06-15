package com.code.aon.ui.payroll.event;

import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.payroll.controller.EmprcccController;
import com.code.aon.ui.payroll.controller.IPayrollConstants;

public class EmprcccControllerListener extends ControllerAdapter implements
		IPayrollConstants {

	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		
		((EmprcccController)getController()).generateCdg();
	}


}
