package com.code.aon.ui.payroll.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import com.code.aon.payroll.enumeration.IndicadorDias;
import com.code.aon.payroll.enumeration.TipoConvenio;

public class ConveniosColectivoController extends PayrollBasicController {

	private List<SelectItem> indicadorDia;
	private List<SelectItem> tipoCon;

	
	/**
	 * Recupera el listado de indices de complementos 
	 * 
	 * @return
	 */
	public List<SelectItem> getListaIndicadorDias() {
		if(indicadorDia==null){
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			indicadorDia = new LinkedList<SelectItem>();
			for (IndicadorDias id : IndicadorDias.values()) {
				String name = id.getName( locale );
				SelectItem item = new SelectItem( id, name );
				indicadorDia.add(item);
			}
		}
		return indicadorDia;
	}
	
	/**
	 * Recupera el listado de indices de complementos 
	 * 
	 * @return
	 */
	public List<SelectItem> getListaTipoConvenios() {
		if(tipoCon==null){
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			tipoCon = new LinkedList<SelectItem>();
			for (TipoConvenio tc : TipoConvenio.values()) {
				String name = tc.getName( locale );
				SelectItem item = new SelectItem( tc, name );
				tipoCon.add(item);
			}
		}
		return tipoCon;
	}

}
