package com.code.aon.ui.payroll.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import com.code.aon.payroll.enumeration.IndicadorAnio;
import com.code.aon.payroll.enumeration.TipoProrrateo;
import com.code.aon.ui.form.LinesController;

public class PagaextLinesController extends LinesController {

	private List<SelectItem> indicadorAnio;
	private List<SelectItem> tipoProrrateo;
	
	/**
	 * Recupera el listado de indicadores de año
	 * 
	 * @return
	 */
	public List<SelectItem> getListaIndicadorAnio() {
		if(indicadorAnio==null){
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			indicadorAnio = new LinkedList<SelectItem>();
			for (IndicadorAnio ia : IndicadorAnio.values()) {
				String name = ia.getName( locale );
				SelectItem item = new SelectItem( ia, name );
				indicadorAnio.add(item);
			}
		}
		return indicadorAnio;
	}
	
	/**
	 * Recupera el listado de tipos de prorrateo 
	 * 
	 * @return
	 */
	public List<SelectItem> getListaTipoProrrateo() {
		if(tipoProrrateo==null){
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			tipoProrrateo = new LinkedList<SelectItem>();
			for (TipoProrrateo tp : TipoProrrateo.values()) {
				String name = tp.getName( locale );
				SelectItem item = new SelectItem( tp, name );
				tipoProrrateo.add(item);
			}
		}
		return tipoProrrateo;
	}

}
