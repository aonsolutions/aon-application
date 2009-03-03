package com.code.aon.ui.payroll.event;

import com.code.aon.payroll.principales.personas.Trabdto;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.payroll.controller.IPayrollConstants;
import com.code.aon.ui.payroll.controller.Utils;

public class TrabdtoControllerListener extends ControllerAdapter implements
		IPayrollConstants {

	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
			
		super.afterBeanCreated(event);
	}

	@Override
	public void beforeBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		int cdg = ((Trabdto)this.getController().getTo()).getEmprper().getCdg();
		int orden = Integer.parseInt(Utils.maxCode("Trabdto", "id.orden")); 
		
		((Trabdto)this.getController().getTo()).getId().setCdg(cdg);
		((Trabdto)this.getController().getTo()).getId().setOrden(orden+1);
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
