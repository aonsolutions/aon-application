package com.code.aon.ui.payroll.event;

import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.payroll.controller.EmprccosController;
import com.code.aon.ui.payroll.controller.IPayrollConstants;

public class EmprccosControllerListener extends ControllerAdapter implements
		IPayrollConstants {

	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		
		((EmprccosController)getController()).generateCdg();
	}

}
