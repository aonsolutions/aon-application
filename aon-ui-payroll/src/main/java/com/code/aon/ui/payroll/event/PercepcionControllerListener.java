package com.code.aon.ui.payroll.event;

import com.code.aon.payroll.auxiliares.convenios.Convenio;
import com.code.aon.payroll.auxiliares.convenios.Nivel;
import com.code.aon.payroll.auxiliares.convenios.Percepcion;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.payroll.controller.IPayrollConstants;
import com.code.aon.ui.util.AonUtil;

public class PercepcionControllerListener extends ControllerAdapter implements IPayrollConstants {
	
	
	

	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		// TODO Auto-generated method stub
		System.out.println("PercepcionControllerListener -------> afterBeanCreated");
		
		
		Nivel nivel = (Nivel)(AonUtil.getController(IPayrollConstants.NIVEL_CONTROLLER_NAME)).getTo();
		((Percepcion)(event.getController().getTo())).setConvenio(nivel.getConvenio());
		((Percepcion)(event.getController().getTo())).setNivel(nivel);
	}
	
	
	
	@Override
	public void afterBeanCanceled(ControllerEvent event)
			throws ControllerListenerException {
		System.out.println("PercepcionControllerListener -------> afterBeanCanceled");
	}
	
	@Override
	public void beforeBeanCanceled(ControllerEvent event)
			throws ControllerListenerException {
		System.out.println("PercepcionControllerListener -------> beforeBeanCanceled");
	}


}
