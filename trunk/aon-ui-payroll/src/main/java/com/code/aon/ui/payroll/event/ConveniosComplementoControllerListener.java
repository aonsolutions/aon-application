package com.code.aon.ui.payroll.event;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.payroll.auxiliares.convenios.Complemento;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.payroll.controller.ConveniosComplementoController;
import com.code.aon.ui.payroll.controller.Utils;
import com.code.aon.ui.util.AonUtil;

public class ConveniosComplementoControllerListener extends ControllerAdapter {

	@Override
	public void beforeBeanCreated(ControllerEvent event) throws ControllerListenerException {
		ConveniosComplementoController controller = (ConveniosComplementoController) event.getController();
		try {
			controller.refreshCotizaciones();
			controller.refreshComplementos();
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException( e.getMessage(), e );
		}
	}

	@Override
	public void beforeBeanSelected(ControllerEvent event) throws ControllerListenerException {
		ConveniosComplementoController controller = (ConveniosComplementoController) event.getController();
		try {
			controller.refreshCotizaciones();
			controller.refreshComplementos();
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException( e.getMessage(), e );
		}
	}
	
	@Override
	public void beforeBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		Complemento to = (Complemento) event.getController().getTo();
		
		if(!Utils.validarMascara(to.getCdg(),"#X")){
			FacesMessage fm = 
	    		AonUtil.getMessage( FacesContext.getCurrentInstance(), "aon_payroll_1481", null );
			throw new ControllerListenerException( fm.getSummary() );
		}
		
		if(to.getCdg().equals("9A")||to.getCdg().equals("9z")||to.getCdg().equals("9w")||to.getCdg().equals("9y")){
			FacesMessage fm = 
	    		AonUtil.getMessage( FacesContext.getCurrentInstance(), "aon_payroll_reserved_cdg", null );
			throw new ControllerListenerException( fm.getSummary() );
		}
	}
	
	@Override
	public void beforeBeanUpdated(ControllerEvent event)
			throws ControllerListenerException {
		beforeBeanAdded(event);
	}
	
}
