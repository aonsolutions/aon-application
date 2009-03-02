package com.code.aon.ui.payroll.controller;

import com.code.aon.payroll.auxiliares.convenios.Complemento;
import com.code.aon.payroll.avanzadas.hojastrabajo.Httcomplemento;
import com.code.aon.payroll.avanzadas.hojastrabajo.Httrabajador;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.LinesController;


public class HttcomplementoController extends LinesController {
	

	private Complemento complemento;

	public Complemento getComplemento() {
		return complemento;
	}

	public void setComplemento(Complemento complemento) {
		this.complemento = complemento;
	}
	
	/**
	 * genera un cdg siguiendo al maximo 
	 */
	public void generateCdg(){
		
		Integer cdg= ((Httrabajador)(FormUtil.getController(IPayrollConstants.HTTRABAJADOR_CONTROLLER_NAME)).getTo()).getCdg();
		Integer orden= Integer.parseInt(Utils.maxCode("Httcomplemento","id.orden","id.cdg="+cdg));
		
		((Httcomplemento)getTo()).getId().setCdg(((Httrabajador)(FormUtil.getController(IPayrollConstants.HTTRABAJADOR_CONTROLLER_NAME)).getTo()).getCdg());
		((Httcomplemento)getTo()).getId().setOrden(orden +1);
	}
	
    
	
}


