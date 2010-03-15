package com.code.aon.ui.payroll.event;


import com.code.aon.payroll.geograficas.Provincia;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class ProvinciaControllerListener extends ControllerAdapter {
	
	public void beforeBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		Provincia p = (Provincia)event.getController().getTo();
		
		if (p.getCdg().length()==1)
			p.setCdg("0"+p.getCdg());
	}
	
}
