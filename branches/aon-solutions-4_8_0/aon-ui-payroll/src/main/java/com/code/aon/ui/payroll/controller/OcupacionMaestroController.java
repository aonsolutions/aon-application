package com.code.aon.ui.payroll.controller;

import java.util.Date;

import javax.faces.event.ActionEvent;


import com.code.aon.common.ManagerBeanException;
import com.code.aon.payroll.dao.IPayrollAlias;
import com.code.aon.ui.form.BasicController;

public class OcupacionMaestroController extends BasicController {
	
	private Date searchFecini;
	private Date searchFecfin;
	private boolean searchExclusivo;
	
	//añade el valor de los checkbox al criteria para realizar busquedas
	@Override
	public void onSearch(ActionEvent event) {
		
		try {
			if(searchFecini!=null)
				getCriteria().addEqualExpression(getFieldName(IPayrollAlias.OCUPACION_MAESTRO_OCUPACIONES_ID_FECINI), searchFecini);
			if(searchFecfin!=null)
				getCriteria().addEqualExpression(getFieldName(IPayrollAlias.OCUPACION_MAESTRO_OCUPACIONES_FECFIN), searchFecfin);
			if (searchExclusivo)
				getCriteria().addEqualExpression(getFieldName(IPayrollAlias.OCUPACION_MAESTRO_EXCLUSIVO), "S");
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		searchFecini=null;
		searchFecfin=null;
		searchExclusivo=false;
		
		super.onSearch(event);
	}

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
	
	public boolean getSearchExclusivo() {
		return searchExclusivo;
	}

	public void setSearchExclusivo(boolean searchExclusivo) {
		this.searchExclusivo = searchExclusivo;
	}

}
