package com.code.aon.ui.payroll.controller;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;

import org.apache.commons.beanutils.PropertyUtils;

import com.code.aon.payroll.cotizacion.Linepigr;
import com.code.aon.payroll.enumeration.Epigrafes;
import com.code.aon.payroll.enumeration.Prorateo;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;

public class EpigrafeController extends LinesController {

	@Override
	public void onAccept(ActionEvent event) {
		boolean bol = isNew();
		super.onAccept(event);
		if ( bol )
			super.onReset( event );
	}

	
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
			s = ((Linepigr)this.getTo()).getEpi().getName(locale);
		    return s;
	}
	

}
