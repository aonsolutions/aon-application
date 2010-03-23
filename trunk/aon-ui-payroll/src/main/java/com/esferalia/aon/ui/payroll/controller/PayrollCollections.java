package com.esferalia.aon.ui.payroll.controller;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import com.esferalia.aon.payroll.core.enumeration.TipoContingencia;

public class PayrollCollections implements Serializable {

	private static final long serialVersionUID = -3260568450232494562L;

	private List<SelectItem> tiposContigencia;

	public List<SelectItem> getTiposContingencia() {
		if (tiposContigencia == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			tiposContigencia = new LinkedList<SelectItem>();
			TipoContingencia[] tipos = TipoContingencia.values();
			for (TipoContingencia tp: tipos) {
				String name = tp.getName(locale);
				SelectItem item = new SelectItem(tp, name);
				tiposContigencia.add(item);
			}
		}
		return tiposContigencia;
	}

}
