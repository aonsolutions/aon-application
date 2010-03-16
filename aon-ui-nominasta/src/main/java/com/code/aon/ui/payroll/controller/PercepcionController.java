package com.code.aon.ui.payroll.controller;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.payroll.dao.IPayrollAlias;
import com.code.aon.payroll.enumeration.TipoPercepcion;

public class PercepcionController extends PayrollBasicController {
	private Date searchFecini;
	private Date searchFecfin;

	public Date getSearchFecini() {
		return searchFecini;
	}

	public void setSearchFecini(Date searchFecini) {
		this.searchFecini = searchFecini;
	}

	public Date getSearchFecfin() {
		return searchFecfin;
	}

	public void setSearchFecfin(Date searchFecfin) {
		this.searchFecfin = searchFecfin;
	}

	@Override
	public void onSearch(ActionEvent event) {

		try {
			if (searchFecini != null) {
				getCriteria().addEqualExpression(
						getFieldName(IPayrollAlias.PERCEPCION_LINPERCEPCIONES_ID_FECINI), searchFecini);
			}
			if (searchFecfin != null) {

				getCriteria().addEqualExpression(
						getFieldName(IPayrollAlias.PERCEPCION_LINPERCEPCIONES_FECFIN), searchFecfin);
			}
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		searchFecini = null;
		searchFecfin = null;

		super.onSearch(event);
	}
	
	private List<SelectItem> tipos;

	/**
	 * Recupera el enumerado de Desempleados.
	 * 
	 * @return
	 */
	public List<SelectItem> getListaTipos() {
		if ( tipos == null ) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			tipos = new LinkedList<SelectItem>();
			for (TipoPercepcion tipo : TipoPercepcion.values()) {
				String name = tipo.getName( locale );
				SelectItem item = new SelectItem( tipo, name );
				tipos.add(item);
			}
		}
		return tipos;
	}

}
