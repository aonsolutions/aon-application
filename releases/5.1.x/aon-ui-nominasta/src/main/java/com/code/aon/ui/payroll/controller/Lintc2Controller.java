package com.code.aon.ui.payroll.controller;

import com.code.aon.payroll.principales.personas.Persona;
import com.code.aon.payroll.resultados.seguros.Lintc2;
import com.code.aon.payroll.resultados.seguros.Tc2;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.LinesController;

public class Lintc2Controller extends LinesController {


	
	
	
	
    
	/**
	 * genera un cdg siguiendo al maximo 
	 */
	public void generateCdg(){
		
		Integer cdg= ((Tc2)(FormUtil.getController(IPayrollConstants.TC2_CONTROLLER_NAME)).getTo()).getCdg();
		Integer orden= Integer.parseInt(Utils.maxCode("Lintc2","id.linea"));
		
		((Lintc2)getTo()).getId().setCdg(cdg);
		((Lintc2)getTo()).getId().setLinea(orden +1);
		
		 Persona p= new Persona();
		 p.setCdg(1);
	    ((Lintc2)getTo()).setPersona(p);
	
}
}
