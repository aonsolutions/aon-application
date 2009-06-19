package com.code.aon.ui.payroll.event;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.payroll.auxiliares.organismosyentidades.Delegacion;
import com.code.aon.payroll.cotizacion.Porcentaje;
import com.code.aon.payroll.geograficas.Nacion;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.payroll.controller.Utils;
import com.code.aon.ui.util.AonUtil;

public class DelegacionControllerListener extends ControllerAdapter {	
	




	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
	    Delegacion d = (Delegacion) event.getController().getTo();
	
			if (d.getCodpos().length()<5 || d.getCodpos().length()>=5){				
		    	FacesMessage fm = 
		    		AonUtil.getMessage( FacesContext.getCurrentInstance(),"aon_payroll_1453", null );
				   throw new ControllerListenerException( fm.getSummary() );
			}

			
		
	}

	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
	    Delegacion d = (Delegacion) event.getController().getTo();
	
			if (d.getCodpos().length()<5 ){
				FacesMessage fm = 
		    		AonUtil.getMessage( FacesContext.getCurrentInstance(),"aon_payroll_1453", null );
				 throw new ControllerListenerException();
			}

			
		
	}

}
	

