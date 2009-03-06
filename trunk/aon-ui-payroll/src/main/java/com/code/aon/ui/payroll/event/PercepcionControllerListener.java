package com.code.aon.ui.payroll.event;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import com.code.aon.payroll.avanzadas.kartel.Percepcion;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;

public class PercepcionControllerListener extends ControllerAdapter {

	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		// TODO Auto-generated method stub
		System.out.println("afterBeanCreated");
		super.afterBeanCreated(event);
	}

	@Override
	public void afterBeanReset(ControllerEvent event)
			throws ControllerListenerException {
		// TODO Auto-generated method stub
		System.out.println("afterBeanReset");
		super.afterBeanReset(event);
	}

	@Override
	public void afterModelInitialized(ControllerEvent event)
			throws ControllerListenerException {
		// TODO Auto-generated method stub
		System.out.println("afterModelInitialized");
		super.afterModelInitialized(event);
	}

	@Override
	public void beforeBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		// TODO Auto-generated method stub
		System.out.println("beforeBeanSelected");
		super.beforeBeanSelected(event);
	}
	
	@Override
	public void beforeBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		// TODO Auto-generated method stub
		System.out.println("beforeBeanAdded");
		comprobarCdg();
		
		super.beforeBeanAdded(event);
	}
	
	@Override
	public void beforeBeanUpdated(ControllerEvent event)
			throws ControllerListenerException {
		// TODO Auto-generated method stub
		System.out.println("beforeBeanUpdated");
		comprobarCdg();
		
		super.beforeBeanUpdated(event);
	}
	
	/**
	 * Comprueba que el codigo sea de 8 caracteres
	 * @throws ControllerListenerException 
	 */
	private void comprobarCdg() throws ControllerListenerException{
		if(((Percepcion)getController().getTo()).getCdg().length()<8){
			FacesMessage fm = AonUtil.getMessage( FacesContext.getCurrentInstance(), "aon_payroll_cdg_length", null );
			throw new ControllerListenerException( fm.getSummary() );
		}	
	}
	

}
