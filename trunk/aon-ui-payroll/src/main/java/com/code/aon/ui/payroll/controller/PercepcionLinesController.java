package com.code.aon.ui.payroll.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import com.code.aon.payroll.enumeration.PagaExtra;
import com.code.aon.ui.form.LinesController;


public class PercepcionLinesController extends LinesController {

	private List<SelectItem> pagas;

	/**
	 * Recupera los tipos de redondeo de pagas extra 
	 * 
	 * @return
	 */
	public List<SelectItem> getListaPagaExtra() {
		if(pagas==null){
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			pagas = new LinkedList<SelectItem>();
			for (PagaExtra paga : PagaExtra.values()) {
				String name = paga.getName( locale );
				SelectItem item = new SelectItem( paga, name );
				pagas.add(item);
			}
		}
		return pagas;
	}

}
