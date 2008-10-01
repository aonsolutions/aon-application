package com.code.aon.ui.payroll.event;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import com.code.aon.payroll.cotizacion.Bonificacion;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;


public class BonificacionControllerListener extends ControllerAdapter {

	@Override
	public void beforeBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		
		IController c = event.getController();
		Bonificacion bon = (Bonificacion) c.getTo();
		
		if (bon.getCdg()==0) {
			FacesMessage fm = 
	    		AonUtil.getMessage( FacesContext.getCurrentInstance(), "aon_payroll_reserved_cdg", null );
			throw new ControllerListenerException( fm.getSummary() );
		}
	}

}
