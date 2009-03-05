package com.code.aon.ui.payroll.event;

import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.payroll.controller.ActividadController;
import com.code.aon.ui.payroll.controller.HttcomplementoController;
import com.code.aon.ui.payroll.controller.HttrabajadorController;
import com.code.aon.ui.payroll.controller.IPayrollConstants;
import com.code.aon.ui.payroll.controller.Lintc2Controller;
import com.code.aon.ui.payroll.controller.PersonaController;

public class Lintc2ControllerListener extends ControllerAdapter implements
		IPayrollConstants {

	
	
	@Override
	public void beforeBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		
		((Lintc2Controller)getController()).generateCdg();
	}


}
