package com.code.aon.ui.payroll.event;

import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.payroll.controller.IPayrollConstants;
import com.code.aon.ui.payroll.controller.Tc2Controller;

public class Tc2ControllerListener extends ControllerAdapter implements
		IPayrollConstants {


	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		
		((Tc2Controller)getController()).generateCdg();
	}

}
