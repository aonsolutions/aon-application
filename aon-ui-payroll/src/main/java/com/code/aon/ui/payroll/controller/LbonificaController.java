package com.code.aon.ui.payroll.controller;

import com.code.aon.payroll.avanzadas.simulacion.Costes;
import com.code.aon.payroll.avanzadas.simulacion.Lbonifica;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.LinesController;

public class LbonificaController extends LinesController {


	
	
	
	
    
	/**
	 * genera un cdg siguiendo al maximo 
	 */
public void generateCdg(){
		
		Integer cdg= ((Costes)(FormUtil.getController(IPayrollConstants.COSTES_CONTROLLER_NAME)).getTo()).getId().getCdg();
		Integer numero= Integer.parseInt(Utils.maxCode("Lbonifica","id.numero"));
		
		((Lbonifica)getTo()).getId().setCdg(((Costes)(FormUtil.getController(IPayrollConstants.COSTES_CONTROLLER_NAME)).getTo()).getId().getCdg());
		((Lbonifica)getTo()).getId().setNumero(numero +1);
	}
	
	
	
}
