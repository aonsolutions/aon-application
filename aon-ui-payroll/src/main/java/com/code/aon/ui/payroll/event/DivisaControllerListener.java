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


import com.code.aon.payroll.auxiliares.convenios.Convenio;
import com.code.aon.payroll.cotizacion.Linepigr;
import com.code.aon.payroll.divisa.Divisa;
import com.code.aon.payroll.divisa.LinDivisa;
import com.code.aon.payroll.divisa.LinDivisaId;

import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.payroll.controller.ContratosInternosController;
import com.code.aon.ui.payroll.controller.DivisaController;
import com.code.aon.ui.payroll.controller.DivisaMaestroController;
import com.code.aon.ui.payroll.controller.IPayrollConstants;
import com.code.aon.ui.util.AonUtil;

public class DivisaControllerListener extends ControllerAdapter {

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		
		/*	 LinDivisa Div = (LinDivisa)event.getController().getTo();
		Divisa d = (Divisa)event.getController().getTo();
		
		if(Div.getFecfin()==null) {
			Calendar c = Calendar.getInstance(); 
			c.set(9999, 12, 31);
			Div.setFecfin(c.getTime());
		} 
		
			
		if(Div.getFecfin().before(Div.getId().getFecini())) {
			FacesMessage fm = 
	    		AonUtil.getMessage( FacesContext.getCurrentInstance(), "aon_payroll_1405", null );
			throw new ControllerListenerException( fm.getSummary() );
		}
		
		
		if(Div.getId().getDivisaFinal()== null)
		{
			System.out.println("INSERTAR DIVISA FINALLLLLLLLL");
			
	        Div.getId().setDivisaFinal(d.getCdg());
		}		
	
	*/
		
		
		String dF = ((Divisa)(AonUtil.getController(IPayrollConstants.DIVISAMAESTRO_CONTROLLER_NAME).getTo())).getCdg();
		System.out.println(dF);	
		String cdgg = ((LinDivisa)(AonUtil.getController(IPayrollConstants.DIVISA_CONTROLLER_NAME)).getTo()).getDivisa1().getCdg();
        System.out.println("-----------"+cdgg);	
		String desc = ((LinDivisa)(AonUtil.getController(IPayrollConstants.DIVISA_CONTROLLER_NAME)).getTo()).getDivisa1().getDescription();
        System.out.println("-----------"+desc);			
	    ((LinDivisa)(event.getController().getTo())).getId().setDivisaFinal(dF);
	    ((LinDivisa)(event.getController().getTo())).getDivisa1().setCdg(cdgg);
	    ((LinDivisa)(event.getController().getTo())).getDivisa1().setDescription(desc);
	
	
	}


	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		
		/*	 LinDivisa Div = (LinDivisa)event.getController().getTo();
		Divisa d = (Divisa)event.getController().getTo();
		
		if(Div.getFecfin()==null) {
			Calendar c = Calendar.getInstance(); 
			c.set(9999, 12, 31);
			Div.setFecfin(c.getTime());
		} 
		
			
		if(Div.getFecfin().before(Div.getId().getFecini())) {
			FacesMessage fm = 
	    		AonUtil.getMessage( FacesContext.getCurrentInstance(), "aon_payroll_1405", null );
			throw new ControllerListenerException( fm.getSummary() );
		}
		
		
		if(Div.getId().getDivisaFinal()== null)
		{
			System.out.println("INSERTAR DIVISA FINALLLLLLLLL");
			
	        Div.getId().setDivisaFinal(d.getCdg());
		}		
	
	*/
		
		
		String dF = ((Divisa)(AonUtil.getController(IPayrollConstants.DIVISAMAESTRO_CONTROLLER_NAME).getTo())).getCdg();
		System.out.println(dF);	
		String cdgg = ((LinDivisa)(AonUtil.getController(IPayrollConstants.DIVISA_CONTROLLER_NAME)).getTo()).getDivisa1().getCdg();
        System.out.println("-----------"+cdgg);	
		String desc = ((LinDivisa)(AonUtil.getController(IPayrollConstants.DIVISA_CONTROLLER_NAME)).getTo()).getDivisa1().getDescription();
        System.out.println("-----------"+desc);			
	    ((LinDivisa)(event.getController().getTo())).getId().setDivisaFinal(dF);
	    ((LinDivisa)(event.getController().getTo())).getDivisa1().setCdg(cdgg);
	    ((LinDivisa)(event.getController().getTo())).getDivisa1().setDescription(desc);
	
	
	}

	
	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		beforeBeanAdded(event);
	}


}
