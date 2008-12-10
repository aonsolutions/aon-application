package com.code.aon.ui.payroll.event;

import com.code.aon.payroll.divisa.Divisa;
import com.code.aon.payroll.divisa.LinDivisa;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.payroll.controller.IPayrollConstants;

public class DivisaControllerListener extends ControllerAdapter {

	@Override
	public void  beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		
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
		
		
		String dF = ((Divisa)(FormUtil.getController(IPayrollConstants.DIVISAMAESTRO_CONTROLLER_NAME).getTo())).getCdg();
		System.out.println(dF);	
		String cdgg = ((LinDivisa)(FormUtil.getController(IPayrollConstants.DIVISA_CONTROLLER_NAME)).getTo()).getDivisa1().getCdg();
        System.out.println("-----------"+cdgg);	
		String desc = ((LinDivisa)(FormUtil.getController(IPayrollConstants.DIVISA_CONTROLLER_NAME)).getTo()).getDivisa0().getDescription();
        System.out.println("-----------"+desc);		
        
        
         String s = ((LinDivisa)(event.getController().getTo())).getDivisa0().getCdg();	   
        ((LinDivisa)(event.getController().getTo())).getId().setCdg(cdgg);	   
	    ((LinDivisa)(event.getController().getTo())).getId().setDivisaFinal(s);
	   // ((LinDivisa)(event.getController().getTo())).getDivisa1().setCdg(cdgg);
	   //((LinDivisa)(event.getController().getTo())).getDivisa1().setDescription(desc);
	
	
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
		
		

	
	}

	
	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		beforeBeanAdded(event);
	}


}
