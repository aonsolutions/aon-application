package com.code.aon.ui.payroll.event;

import com.code.aon.payroll.principales.personas.Prcdivtrab;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.payroll.controller.IPayrollConstants;

public class PrcdivtrabControllerListener extends ControllerAdapter implements
		IPayrollConstants {

	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
			
		super.afterBeanCreated(event);
	}

	@Override
	public void beforeBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		
		int cdg = ((Prcdivtrab)this.getController().getTo()).getEmprper().getCdg();
		((Prcdivtrab)this.getController().getTo()).getId().setCdg(cdg);
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
}
