package com.code.aon.ui.payroll.event;

import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.payroll.controller.HttcomplementoController;
import com.code.aon.ui.payroll.controller.IPayrollConstants;

public class HttcomplementoControllerListener extends ControllerAdapter implements
		IPayrollConstants {

	@Override
	public void beforeBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		
		((HttcomplementoController)getController()).generateCdg();
	}


}
