package com.code.aon.ui.payroll.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import com.code.aon.payroll.enumeration.EnvioSS2;
import com.code.aon.payroll.enumeration.Inddias;
import com.code.aon.payroll.enumeration.Representantes;

public class EmprctraController extends PayrollBasicController {


	
	private List<SelectItem> listaenvioss;

	private List<SelectItem> inndia;

	private List<SelectItem> represen;
	


	

	
	
	
	public List<SelectItem> getListaenvios() {
		if(listaenvioss==null){
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			listaenvioss = new LinkedList<SelectItem>();
			for (EnvioSS2 e : EnvioSS2.values()) {
				String name = e.getName( locale );
				SelectItem item = new SelectItem( e, name );
				listaenvioss.add(item);
			}
		}
		return listaenvioss;
	}
	
	
	public List<SelectItem> getListainddia() {
		if(inndia==null){
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			inndia = new LinkedList<SelectItem>();
			for (Inddias e : Inddias.values()) {
				String name = e.getName( locale );
				SelectItem item = new SelectItem( e, name );
				inndia.add(item);
			}
		}
		return inndia;
	}



	
	public List<SelectItem> getListarepre() {
		if(represen==null){
			
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			represen = new LinkedList<SelectItem>();
			for (Representantes e : Representantes.values()) {
				String name = e.getName( locale );
				SelectItem item = new SelectItem( e, name );
				represen.add(item);
			}
		}
		return represen;
	}
	
}
