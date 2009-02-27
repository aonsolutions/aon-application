package com.code.aon.ui.payroll.event;

import com.code.aon.payroll.principales.persona.Embargo;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class EmbargoBasicControllerListener extends ControllerAdapter {

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		Integer cdg = ((Embargo)this.getController().getTo()).getTrabajador().getCdg();
		Integer codPer = ((Embargo)this.getController().getTo()).getTrabajador().getPersona().getCdg();
		((Embargo)this.getController().getTo()).setCodper(codPer);
		((Embargo)this.getController().getTo()).getId().setCdg(cdg);
	}	
}
