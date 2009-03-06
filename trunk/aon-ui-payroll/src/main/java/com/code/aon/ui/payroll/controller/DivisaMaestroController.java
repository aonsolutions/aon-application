package com.code.aon.ui.payroll.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import com.code.aon.payroll.divisa.Divisa;
import com.code.aon.payroll.enumeration.Divisas;

public class DivisaMaestroController extends PayrollBasicController {

	public void onExit(ActionEvent event) {
		// TODO Auto-generated method stub
	}
Divisa d;

	
private List<SelectItem> redondeos;

     String maestro;
     
     public void getCodigo() {
    	 
    	 
    	this.setMaestro(((Divisa)getTo()).getCdg());
     }
	
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

	public String getMaestro() {
		return maestro;
	}

	public void setMaestro(String maestro) {
		this.maestro = maestro;
	}

	public Divisa getD() {
		return d;
	}

	public void setD(Divisa d) {
		this.d = d;
	}



	
	
	
	
}
