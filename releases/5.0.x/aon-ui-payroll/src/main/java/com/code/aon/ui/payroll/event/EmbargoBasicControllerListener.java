package com.code.aon.ui.payroll.event;

import com.code.aon.payroll.principales.persona.Embargo;
import com.code.aon.payroll.principales.persona.Trabajador;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.payroll.controller.IPayrollConstants;

public class EmbargoBasicControllerListener extends ControllerAdapter {

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		Integer cdg = ((Embargo)this.getController().getTo()).getTrabajador().getCdg();
		Integer codPer = ((Embargo)this.getController().getTo()).getTrabajador().getPersona().getCdg();
		((Embargo)this.getController().getTo()).setCodper(codPer);
		((Embargo)this.getController().getTo()).getId().setCdg(cdg);
	}	
	
	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		//updateBasicModelFromLines();
	}
	
	public void updateBasicModelFromLines(){
		Trabajador trab = (Trabajador)FormUtil.getController(IPayrollConstants.TRABAJADOR_CONTROLLER_NAME).getTo();
		
		((Embargo)this.getController().getTo()).setTrabajador(trab);
		((Embargo)this.getController().getTo()).setCodper(trab.getPersona().getCdg());
	}
	
	
}
