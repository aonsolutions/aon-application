package com.code.aon.ui.payroll.event;



import java.math.BigDecimal;
import java.util.Calendar;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import com.code.aon.payroll.cotizacion.Ocupacion;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;

public class CnaeControllerListener extends ControllerAdapter {

	@SuppressWarnings("unchecked")
	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		
		
	}

	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		beforeBeanAdded(event);
	}
	
}
