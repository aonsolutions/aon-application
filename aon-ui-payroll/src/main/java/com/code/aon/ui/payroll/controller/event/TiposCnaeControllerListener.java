package com.code.aon.ui.payroll.controller.event;

import com.code.aon.payroll.tipos.Cnae;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;


public class TiposCnaeControllerListener extends ControllerAdapter {
	
	/**
	 * Comprueba que la clave no sea ni vacía ni esté fuera del rango
	 */
	@Override
	public void beforeBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
			
		String cdg = ((Cnae)event.getController().getTo()).getCdg();
		System.out.println("--------------"+cdg);
		
		if(cdg.isEmpty())
			AonUtil.addWarningMessage("La clave CNAE pueden estar vacía.");
		else if(cdg.length()<1 || cdg.length()>5)
			AonUtil.addWarningMessage("La clave CNAE debe tener una longitud de 1 a 5.");
		
		
		
	}

}
