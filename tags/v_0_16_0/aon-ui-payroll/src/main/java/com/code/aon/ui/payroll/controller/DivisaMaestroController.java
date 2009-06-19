package com.code.aon.ui.payroll.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import com.code.aon.payroll.enumeration.Divisas;
import com.code.aon.ui.form.BasicController;

public class DivisaMaestroController extends PayrollBasicController {

	public void onExit(ActionEvent event) {
		// TODO Auto-generated method stub
	}

	
private List<SelectItem> redondeos;


	
	
	/**
	 * Recupera los tipos de retribuciones 
	 * 
	 * @return
	 */
	public List<SelectItem> getListaRedondeos() {
		if(redondeos==null){
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			redondeos = new LinkedList<SelectItem>();
			for (Divisas p : Divisas.values()) {
				String name = p.getName( locale );
				SelectItem item = new SelectItem( p, name );
				redondeos.add(item);
			}
		}
		return redondeos;
	}
	
	
	
	
	
}
