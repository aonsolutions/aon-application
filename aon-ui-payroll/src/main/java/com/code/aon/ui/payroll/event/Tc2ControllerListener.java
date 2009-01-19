package com.code.aon.ui.payroll.event;

import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.payroll.controller.ActividadController;
import com.code.aon.ui.payroll.controller.IPayrollConstants;
import com.code.aon.ui.payroll.controller.NominaController;
import com.code.aon.ui.payroll.controller.PersonaController;

public class Tc2ControllerListener extends ControllerAdapter implements
		IPayrollConstants {


	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		
		((NominaController)getController()).generateCdg();
	}

}
