package com.code.aon.ui.payroll.event;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import com.code.aon.payroll.auxiliares.convenios.Categoria;
import com.code.aon.payroll.auxiliares.convenios.Nivel;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.payroll.controller.IPayrollConstants;
import com.code.aon.ui.payroll.controller.Utils;
import com.code.aon.ui.util.AonUtil;

public class CategoriaControllerListener extends ControllerAdapter implements IPayrollConstants {
	
	
	@Override
	public void beforeBeanReset(ControllerEvent event)
			throws ControllerListenerException {
		System.out.println("CategoriaControllerListener -------> beforeBeanReset");
	}

	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		 beforeBeanReset(event);
		 
		 Nivel nivel = (Nivel)(FormUtil.getController(IPayrollConstants.NIVEL_CONTROLLER_NAME)).getTo();
		 Categoria c = (Categoria)(event.getController().getTo());
		 c.setNivel(nivel);
		 c.setCdgnivel(nivel.getId().getCdg());
		 c.setConvenio(nivel.getConvenio());
	}
	
	
	
	@Override
	public void afterBeanCanceled(ControllerEvent event)
			throws ControllerListenerException {
		System.out.println("CategoriaControllerListener -------> afterBeanCanceled");
	}
	
	@Override
	public void beforeBeanCanceled(ControllerEvent event)
			throws ControllerListenerException {
		System.out.println("CategoriaControllerListener -------> beforeBeanCanceled");
	}
	
	@Override
	public void beforeBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		Categoria to = (Categoria) event.getController().getTo();
		
		setRequiredData(event);
		
		if(!(Utils.validarMascara(to.getId().getCdg(),"#X"))){
			FacesMessage fm = 
	    		AonUtil.getMessage( FacesContext.getCurrentInstance(), "aon_payroll_1481", null );
			throw new ControllerListenerException( fm.getSummary() );
		}
		
	}
	
	/**
	 * Establece atributos necesarios para la clave principal obtenidos del maestro
	 * @param event
	 */
	private void setRequiredData(ControllerEvent event){
		Nivel nivel = (Nivel)(FormUtil.getController(IPayrollConstants.NIVEL_CONTROLLER_NAME)).getTo();
		Categoria c = (Categoria)(event.getController().getTo());
		
		c.getId().setCodcon(nivel.getConvenio().getCdg());
	}


}
