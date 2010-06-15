package com.code.aon.ui.payroll.event;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import com.code.aon.payroll.auxiliares.convenios.Convenio;
import com.code.aon.payroll.auxiliares.convenios.Pagaext;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.payroll.controller.IPayrollConstants;
import com.code.aon.ui.payroll.controller.Utils;
import com.code.aon.ui.util.AonUtil;

public class PagaextControllerListener extends ControllerAdapter {

	@Override
	public void beforeBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		Pagaext p=(Pagaext)(event.getController().getTo());
		
		if(!Utils.isFechaDDMM(p.getPerini()) || !Utils.isFechaDDMM(p.getPerfin()) || !Utils.isFechaDDMM(p.getFeccob())){
			FacesMessage fm = 
	    		AonUtil.getMessage( FacesContext.getCurrentInstance(), "aon_payroll_1414", null );
			throw new ControllerListenerException( fm.getSummary() );
		}
		
		Convenio convenio = (Convenio)FormUtil.getController(IPayrollConstants.CONVENIO_CONTROLLER_NAME).getTo();
		
		p.getId().setCodcom(p.getComplemento().getCdg());
		
	}

	@Override
	public void beforeBeanUpdated(ControllerEvent event)
			throws ControllerListenerException {
		
	}
	
	@Override
	public void beforeBeanReset(ControllerEvent event)
			throws ControllerListenerException {
		// TODO Auto-generated method stub
		super.beforeBeanReset(event);
	}
	
}
