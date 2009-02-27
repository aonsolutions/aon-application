package com.code.aon.ui.payroll.event;


import com.code.aon.common.ManagerBeanException;

import com.code.aon.payroll.principales.personas.Otrperc;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.payroll.controller.EmpreactController;
import com.code.aon.ui.payroll.controller.IPayrollConstants;
import com.code.aon.ui.payroll.controller.OtraspercepController;
import com.code.aon.ui.payroll.controller.OtrpercepController;


public class OtraspercepControllerListener extends ControllerAdapter implements IPayrollConstants {
	
	
	
	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {

		((OtraspercepController) getController()).generateCdg();
	}

	@Override
	public void beforeBeanAdded(ControllerEvent event)
			throws ControllerListenerException {

		((OtraspercepController) getController()).generateCdg();

	}

}
