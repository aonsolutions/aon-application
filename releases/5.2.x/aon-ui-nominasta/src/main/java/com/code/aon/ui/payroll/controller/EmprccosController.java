package com.code.aon.ui.payroll.controller;

import com.code.aon.payroll.principales.empresa.Emprccos;
import com.code.aon.ui.form.LinesController;

public class EmprccosController extends LinesController {


	
	
	
	
    
	/**
	 * genera un cdg siguiendo al maximo 
	 */
	public void generateCdg(){
		((Emprccos)getTo()).setCdg(Integer.parseInt(Utils.maxCode("Emprccos", "cdg"))+1);
	}
	
	
	
}
