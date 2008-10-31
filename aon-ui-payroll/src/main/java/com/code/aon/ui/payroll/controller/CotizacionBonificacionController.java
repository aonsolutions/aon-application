package com.code.aon.ui.payroll.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.payroll.dao.IPayrollAlias;

public class CotizacionBonificacionController extends PayrollBasicController{
	
	private boolean searchBoniss;
	private boolean searchMayor60;
	private boolean searchRdl052006;
	private boolean searchRestait;
	
	//añade el valor de los checkbox al criteria para realizar busquedas
	@Override
	public void onSearch(ActionEvent event) {
		
		try {
			if(searchBoniss)
				getCriteria().addEqualExpression(getFieldName(IPayrollAlias.BONIFICACION_BONISS), "S");
			if(searchMayor60)
				getCriteria().addEqualExpression(getFieldName(IPayrollAlias.BONIFICACION_MAYOR60), "S");
			if(searchRdl052006)
				getCriteria().addEqualExpression(getFieldName(IPayrollAlias.BONIFICACION_RDL052006), "S");
			if(searchRestait)
				getCriteria().addEqualExpression(getFieldName(IPayrollAlias.BONIFICACION_RESTAIT), "S");
				
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		searchBoniss=false;
		searchMayor60=false;
		searchRdl052006=false;
		searchRestait=false;		
		
		
		super.onSearch(event);
	}
	

	public boolean getSearchBoniss() {
		return searchBoniss;
	}

	public void setSearchBoniss(boolean searchBoniss) {
		this.searchBoniss = searchBoniss;
	}
	
	public boolean getSearchMayor60() {
		return searchMayor60;
	}

	public void setSearchMayor60(boolean searchMayor60) {
		this.searchMayor60 = searchMayor60;
	}
	
	public boolean getSearchRdl052006() {
		return searchRdl052006;
	}

	public void setSearchRdl052006(boolean searchRdl052006) {
		this.searchRdl052006 = searchRdl052006;
	}
	
	public boolean getSearchRestait() {
		return searchRestait;
	}

	public void setSearchRestait(boolean searchRestait) {
		this.searchRestait = searchRestait;
	}


}
