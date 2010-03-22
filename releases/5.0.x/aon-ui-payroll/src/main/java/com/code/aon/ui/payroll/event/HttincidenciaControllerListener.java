package com.code.aon.ui.payroll.event;

import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.payroll.controller.HttincidenciasController;
import com.code.aon.ui.payroll.controller.IPayrollConstants;

public class HttincidenciaControllerListener extends ControllerAdapter implements
		IPayrollConstants {

	@Override
	public void beforeBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		
		((HttincidenciasController)getController()).generateCdg();
	}


}
