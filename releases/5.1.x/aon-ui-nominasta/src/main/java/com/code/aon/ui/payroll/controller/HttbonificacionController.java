package com.code.aon.ui.payroll.controller;

import com.code.aon.payroll.avanzadas.hojastrabajo.Httbonificacion;
import com.code.aon.payroll.avanzadas.hojastrabajo.Httrabajador;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.LinesController;


public class HttbonificacionController extends LinesController {
	
	/**
	 * genera un cdg siguiendo al maximo 
	 */
	public void generateCdg(){
		
		Integer cdg= ((Httrabajador)(FormUtil.getController(IPayrollConstants.HTTRABAJADOR_CONTROLLER_NAME)).getTo()).getCdg();
		Integer orden= Integer.parseInt(Utils.maxCode("Httbonificacion","id.orden","id.cdg="+cdg));
		
		((Httbonificacion)getTo()).getId().setCdg(((Httrabajador)(FormUtil.getController(IPayrollConstants.HTTRABAJADOR_CONTROLLER_NAME)).getTo()).getCdg());
		((Httbonificacion)getTo()).getId().setOrden(orden +1);
	}
	
}


