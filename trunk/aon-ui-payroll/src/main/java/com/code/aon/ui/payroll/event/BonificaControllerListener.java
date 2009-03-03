package com.code.aon.ui.payroll.event;

import com.code.aon.payroll.principales.personas.Bonifica;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.payroll.controller.IPayrollConstants;

public class BonificaControllerListener extends ControllerAdapter implements
		IPayrollConstants {

	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
			
		super.afterBeanCreated(event);
	}

	@Override
	public void beforeBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		
		int cdg = ((Bonifica)this.getController().getTo()).getTipboni().getCdg();
		int numero = ((Bonifica)this.getController().getTo()).getEmprper().getCdg();
		
		((Bonifica)this.getController().getTo()).getId().setCdg(cdg);
		((Bonifica)this.getController().getTo()).getId().setNumero(numero);
		
	}
	
	@Override
	public void beforeBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		// TODO Auto-generated method stub
		System.out.println(" beforeBeanSelected ");

		super.beforeBeanSelected(event);
	}
	
	@Override
	public void beforeModelInitialized(ControllerEvent event)
			throws ControllerListenerException {
		// TODO Auto-generated method stub
		
		super.beforeModelInitialized(event);
	}
	
	@Override
	public void beforeBeanUpdated(ControllerEvent event)
			throws ControllerListenerException {
		((Bonifica)this.getController().getTo()).setEmprper(null);
	}
	
	private void setDefaultFields(){
		Bonifica bon = ((Bonifica)this.getController().getTo());
		/*
		horas
		importe
		prorrateo
		numero
		*/
		
		
	}
}
