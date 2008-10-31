package com.code.aon.ui.payroll.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.payroll.dao.IPayrollAlias;

public class TiposIncidenciaController extends PayrollBasicController{
	
	private boolean searchIndresta;
	private boolean searchInddto;

	//añade el valor de los checkbox al criteria para realizar busquedas
	@Override
	public void onSearch(ActionEvent event) {
		
		try {
			if(searchIndresta)
				getCriteria().addEqualExpression(getFieldName(IPayrollAlias.INCIDENCIA_INDRESTA), "S");
			if(searchInddto)
				getCriteria().addEqualExpression(getFieldName(IPayrollAlias.INCIDENCIA_INDDTO), "S");
				
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		searchIndresta=false;
		searchInddto=false;
		
		
		super.onSearch(event);
	}

	public boolean getSearchIndresta() {
		return searchIndresta;
	}

	public void setSearchIndresta(boolean searchIndresta) {
		this.searchIndresta = searchIndresta;
	}
	
	public boolean getSearchInddto() {
		return searchInddto;
	}

	public void setSearchInddto(boolean searchInddto) {
		this.searchInddto = searchInddto;
	}
	
}
