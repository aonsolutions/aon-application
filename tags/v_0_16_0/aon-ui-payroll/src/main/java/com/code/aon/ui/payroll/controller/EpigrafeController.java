package com.code.aon.ui.payroll.controller;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.payroll.cotizacion.Linepigr;
import com.code.aon.payroll.dao.IPayrollAlias;
import com.code.aon.payroll.enumeration.Epigrafes;
import com.code.aon.ui.form.LinesController;

public class EpigrafeController extends LinesController {


	
	private List<SelectItem> epigrafes;

	public List<SelectItem> getListaEpigrafes() {
		if(epigrafes==null){
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			epigrafes = new LinkedList<SelectItem>();
			for (Epigrafes e : Epigrafes.values()) {
				String name = e.getName( locale );
				SelectItem item = new SelectItem( e, name );
				epigrafes.add(item);
			}
		}
		return epigrafes;
	}
	
	public String getEpigraf() {
		
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			String s ;
			s = ((Linepigr)this.getTo()).getIndit().getName(locale);
		    return s;
	}
	
	



	

}
