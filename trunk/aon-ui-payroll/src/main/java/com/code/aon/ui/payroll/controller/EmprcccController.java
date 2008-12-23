package com.code.aon.ui.payroll.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import com.code.aon.payroll.enumeration.EnvioSS2;
import com.code.aon.payroll.enumeration.Inddias;
import com.code.aon.payroll.enumeration.Representantes;
import com.code.aon.payroll.enumeration.Tipccc;

public class EmprcccController extends PayrollBasicController {


	
	private List<SelectItem> tipccc;


	
	
	public List<SelectItem> getListatipos() {
		if(tipccc==null){
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			tipccc = new LinkedList<SelectItem>();
			for (Tipccc e : Tipccc.values()) {
				String name = e.getName( locale );
				SelectItem item = new SelectItem( e, name );
				tipccc.add(item);
			}
		}
		return tipccc;
	}
	
	



	
	
}
