package com.code.aon.ui.payroll.event;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Calendar;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.validator.ValidatorException;

import org.apache.commons.beanutils.PropertyUtils;

import com.code.aon.payroll.cotizacion.Linepigr;
import com.code.aon.payroll.divisa.Divisa;
import com.code.aon.payroll.divisa.LinDivisa;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;

public class DivisaControllerListener extends ControllerAdapter {

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		
		 LinDivisa Div = (LinDivisa)event.getController().getTo();
				
		if(Div.getFecfin()==null) {
			Calendar c = Calendar.getInstance(); 
			c.set(9999, 12, 31);
			Div.setFecfin(c.getTime());
		} else if(Div.getFecfin().before(Div.getId().getFecini())) {
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
