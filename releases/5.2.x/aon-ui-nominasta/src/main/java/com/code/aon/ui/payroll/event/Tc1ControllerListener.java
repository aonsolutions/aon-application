package com.code.aon.ui.payroll.event;

import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.payroll.controller.IPayrollConstants;
import com.code.aon.ui.payroll.controller.Tc1Controller;

public class Tc1ControllerListener extends ControllerAdapter implements
		IPayrollConstants {

	@Override
	public void beforeBeanAdded(ControllerEvent event)
			throws ControllerListenerException {

		((Tc1Controller) getController()).generateCdg();

	}

	@Override
	public void beforeBeanUpdated(ControllerEvent event)
			throws ControllerListenerException {
		// TODO Auto-generated method stub
		super.beforeBeanUpdated(event);
	}
}
