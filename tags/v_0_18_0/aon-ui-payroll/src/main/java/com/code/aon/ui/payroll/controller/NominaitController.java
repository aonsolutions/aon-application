package com.code.aon.ui.payroll.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import com.code.aon.payroll.enumeration.Tipoit;
import com.code.aon.ui.form.LinesController;

public class NominaitController extends LinesController {
	
	private static final Logger LOGGER = Logger.getLogger(CotizacionBonificacionController.class.getName());
	
	private List<SelectItem> tipoit;
	
	public List<SelectItem> getListaTipoIt() {
		if (tipoit == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			tipoit = new LinkedList<SelectItem>();
			for (Tipoit tit : Tipoit.values()) {
				String name = tit.getName(locale);
				SelectItem item = new SelectItem(tit, name);
				tipoit.add(item);
			}
		}
		return tipoit;
	}

	
	
}
