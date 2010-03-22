package com.code.aon.ui.payroll.event;

import java.util.Calendar;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import com.code.aon.payroll.cotizacion.Elemento;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;

public class ElementoControllerListener extends ControllerAdapter {

	@SuppressWarnings("unchecked")
	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		
		Elemento elem = (Elemento)event.getController().getTo();
				
		if(elem.getFecfin()==null) {
			Calendar c = Calendar.getInstance(); 
			c.set(9999, 12, 31);
			elem.setFecfin(c.getTime());
		} else if(elem.getFecfin().before(elem.getId().getFecini())) {
			FacesMessage fm = 
	    		AonUtil.getMessage( FacesContext.getCurrentInstance(), "aon_payroll_1405", null );
			throw new ControllerListenerException( fm.getSummary() );
		}
		if(elem.getDato1().intValue()<0 || elem.getDato2().intValue()<0){
			FacesMessage fm = 
	    		AonUtil.getMessage( FacesContext.getCurrentInstance(), "aon_payroll_1437", null );
			throw new ControllerListenerException( fm.getSummary() );
		}
		
	}

	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		beforeBeanAdded(event);
	}
	
}
