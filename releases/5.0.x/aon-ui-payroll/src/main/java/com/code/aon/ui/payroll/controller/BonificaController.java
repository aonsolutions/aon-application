package com.code.aon.ui.payroll.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import com.code.aon.payroll.enumeration.TipoImporte;
import com.code.aon.ui.form.LinesController;

public class BonificaController extends LinesController {
	
	private static final Logger LOGGER = Logger.getLogger(CotizacionBonificacionController.class.getName());

	private List<SelectItem> tipoImporte;
	
	public List<SelectItem> getListaTiposImporte() {
		if(tipoImporte==null){
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			tipoImporte = new LinkedList<SelectItem>();
			for (TipoImporte indi : TipoImporte.values()) {
				String name = indi.getName( locale );
				SelectItem item = new SelectItem( indi, name );
				tipoImporte.add(item);
			}
		}
		return tipoImporte;
	}
	
	
}
