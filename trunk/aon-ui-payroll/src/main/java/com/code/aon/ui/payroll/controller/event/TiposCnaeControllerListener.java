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
	
	/* 
	 if lFnEstaVacio(TxtCdg.Text) then begin
         MessageBox("La clave CNAE pueden estar vacía.", "¡Atención!", 48);
         lBolReturn = false;
     end
     else if not (TxtCdg.Text.Trim.Length between 1 and 5) then begin
         MessageBox("La clave CNAE debe tener una longitud de 1 a 5.", "¡Atención!", 48);
         lBolReturn = false;
     end
     else if Sql.Select("SELECT rowid FROM tipocnae WHERE cdg = '" + TxtCdg.Text + "'") then begin
         MessageBox("Clave ya existente en la base de datos.", "¡Atención!", 48);
         lBolReturn = false;
     end
     */
		
		String cdg = ((Cnae)event.getController().getTo()).getCdg();
		System.out.println("--------------"+cdg);
		
		if(cdg.isEmpty())
			AonUtil.addWarningMessage("La clave CNAE pueden estar vacía.");
		else if(cdg.length()<1 || cdg.length()>5)
			AonUtil.addWarningMessage("La clave CNAE debe tener una longitud de 1 a 5.");
		
		
		
	}

}
