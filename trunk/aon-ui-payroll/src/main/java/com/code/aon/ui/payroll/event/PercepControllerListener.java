package com.code.aon.ui.payroll.event;


import com.code.aon.common.ManagerBeanException;

import com.code.aon.payroll.principales.personas.Otrperc;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.payroll.controller.ConveniosComplementoController;
import com.code.aon.ui.payroll.controller.IPayrollConstants;
import com.code.aon.ui.payroll.controller.OtrpercepController;
import com.code.aon.ui.payroll.controller.PercepController;


public class PercepControllerListener extends ControllerAdapter implements IPayrollConstants {
	
	@Override
	public void beforeBeanCreated(ControllerEvent event) throws ControllerListenerException {
		PercepController controller = (PercepController) event.getController();
		try {

			controller.refreshComplementos();
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException( e.getMessage(), e );
		}
	}

	@Override
	public void beforeBeanSelected(ControllerEvent event) throws ControllerListenerException {
		PercepController controller = (PercepController) event.getController();
		try {
	
			controller.refreshComplementos();
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException( e.getMessage(), e );
		}
	}
	
}
