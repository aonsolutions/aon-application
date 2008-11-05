package com.code.aon.ui.payroll.controller;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;


import com.code.aon.payroll.enumeration.EnvioSS;


public class ClienteController extends PayrollBasicController {

	
	private List<SelectItem> envioss;


	
	
	/**
	 * Recupera los tipos de retribuciones 
	 * 
	 * @return
	 */
	public List<SelectItem> getListaEnvioss() {
		if(envioss==null){
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			envioss = new LinkedList<SelectItem>();
			for (EnvioSS p : EnvioSS.values()) {
				String name = p.getName( locale );
				SelectItem item = new SelectItem( p, name );
				envioss.add(item);
			}
		}
		return envioss;
	}

	
	






}
