package com.code.aon.ui.payroll.event;

import java.util.Calendar;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import com.code.aon.payroll.cotizacion.Linbasec;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;

public class BasesControllerListener extends ControllerAdapter {
	
	
	

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		
		 Linbasec lin = (Linbasec)event.getController().getTo();
				
		if(lin.getFecfin()==null) {
			Calendar c = Calendar.getInstance(); 
			c.set(9999, 12, 31);
			lin.setFecfin(c.getTime());
		} else if(lin.getFecfin().before(lin.getId().getFecini())) {
			FacesMessage fm = 
	    		AonUtil.getMessage( FacesContext.getCurrentInstance(), "aon_payroll_1405", null );
			throw new ControllerListenerException( fm.getSummary() );
		}
		if(lin.getMincot().intValue()<0) {
			FacesMessage fm = 
	    		AonUtil.getMessage( FacesContext.getCurrentInstance(), "aon_payroll_1405", null );
			throw new ControllerListenerException( fm.getSummary() );
		}
		
	
		
		
	}

	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		beforeBeanAdded(event);
	}



}
