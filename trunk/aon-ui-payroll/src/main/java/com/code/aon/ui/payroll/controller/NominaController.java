package com.code.aon.ui.payroll.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import com.code.aon.payroll.enumeration.Tipnomina;
import com.code.aon.payroll.principales.empresa.Actividad;
import com.code.aon.payroll.resultados.nomina.Nomina;

public class NominaController extends PayrollBasicController {

	private List<SelectItem> tipnomina;

	public List<SelectItem> getTipnomina() {
		if(tipnomina==null){
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			tipnomina = new LinkedList<SelectItem>();
			for (Tipnomina p : Tipnomina.values()) {
				String name = p.getName( locale );
				SelectItem item = new SelectItem( p, name );
				tipnomina.add(item);
			}
		}
		return tipnomina;
	}
	
	/**
	 * genera un cdg siguiendo al maximo 
	 */
	public void generateCdg(){
		((Nomina)getTo()).setCdg(Integer.parseInt(Utils.maxCode("Nomina", "cdg"))+1);
	}
	
	
	
	
}
