package com.code.aon.ui.payroll.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import com.code.aon.payroll.enumeration.Afectados;
import com.code.aon.payroll.enumeration.ImporteIndicar;
import com.code.aon.ui.form.LinesController;

public class TrabdtoController extends LinesController {

	private static final Logger LOGGER = Logger.getLogger(TrabdtoController.class.getName());

	private List<SelectItem> indimp;
	private List<SelectItem> afectados;

	public List<SelectItem> getListaImporteIndicar() {
		if (indimp == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			indimp = new LinkedList<SelectItem>();
			for (ImporteIndicar indi : ImporteIndicar.values()) {
				String name = indi.getName(locale);
				SelectItem item = new SelectItem(indi, name);
				indimp.add(item);
			}
		}
		return indimp;
	}
	
	/**
	 * Recupera los tipos de retribuciones
	 * 
	 * @return
	 */
	public List<SelectItem> getListaAfectados() {
		if (afectados == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			afectados = new LinkedList<SelectItem>();
			for (Afectados p : Afectados.values()) {
				String name = p.getName(locale);
				SelectItem item = new SelectItem(p, name);
				afectados.add(item);
			}
		}
		return afectados;
	}

}
