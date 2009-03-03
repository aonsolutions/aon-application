package com.code.aon.ui.payroll.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import com.code.aon.payroll.auxiliares.convenios.Categoria;
import com.code.aon.payroll.auxiliares.convenios.Complemento;
import com.code.aon.payroll.avanzadas.hojastrabajo.Httbonificacion;
import com.code.aon.payroll.avanzadas.hojastrabajo.Httincidencia;
import com.code.aon.payroll.avanzadas.hojastrabajo.Httrabajador;
import com.code.aon.payroll.enumeration.EstadoCivil;
import com.code.aon.payroll.enumeration.Timecont;
import com.code.aon.payroll.enumeration.TipIrpf;
import com.code.aon.payroll.enumeration.Tipccc;
import com.code.aon.payroll.enumeration.Prorateo;
import com.code.aon.payroll.principales.personas.Persona;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.LinesController;


public class HttincidenciasController extends LinesController {

	
	
	/**
	 * genera un cdg siguiendo al maximo 
	 */
	public void generateCdg(){
		
		Integer cdg= ((Httrabajador)(FormUtil.getController(IPayrollConstants.HTTRABAJADOR_CONTROLLER_NAME)).getTo()).getCdg();
		Integer orden= Integer.parseInt(Utils.maxCode("Httincidencia","id.orden"));
		
		((Httincidencia)getTo()).getId().setCdg(((Httrabajador)(FormUtil.getController(IPayrollConstants.HTTRABAJADOR_CONTROLLER_NAME)).getTo()).getCdg());
		((Httincidencia)getTo()).getId().setOrden(orden +1);
	}
	
	
}


