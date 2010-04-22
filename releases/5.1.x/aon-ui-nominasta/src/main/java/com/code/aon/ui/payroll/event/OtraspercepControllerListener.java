package com.code.aon.ui.payroll.event;

import com.code.aon.payroll.principales.empresa.Empresa;
import com.code.aon.payroll.principales.personas.Persona;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.payroll.controller.IPayrollConstants;
import com.code.aon.ui.payroll.controller.OtraspercepController;

public class OtraspercepControllerListener extends ControllerAdapter implements IPayrollConstants {
	
	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		((OtraspercepController) getController()).generateCdg();
		((OtraspercepController) getController()).setEmpresa(new Empresa());
		((OtraspercepController) getController()).setPersona(new Persona());
	}

	@Override
	public void beforeBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		((OtraspercepController) getController()).generateCdg();
		((OtraspercepController) getController()).updateBeanJoins();
	}
	
	@Override
	public void beforeBeanUpdated(ControllerEvent event)
			throws ControllerListenerException {
		((OtraspercepController) getController()).updateBeanJoins();
	}
	
	@Override
	public void beforeBeanReset(ControllerEvent event)
			throws ControllerListenerException {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		// TODO Auto-generated method stub
		super.afterBeanSelected(event);
		
		//((OtraspercepController)event.getController()).setPersona(new Persona());
		//Integer cdg = ((Otrperc)event.getController().getTo()).getCodper();
		//((OtraspercepController)event.getController()).getPersona().setCdg(cdg);
	}
	
	
	
	
	
	
	

}
