package com.code.aon.ui.payroll.controller;

import java.util.Date;

import javax.faces.event.ActionEvent;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.payroll.dao.IPayrollAlias;

public class MutuasController extends PayrollBasicController {

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

	//añade la fecha al criteria para realizar busquedas
	@Override
	public void onSearch(ActionEvent event) {
	
		try {
			if(searchFecini != null){
				getCriteria().addEqualExpression(getFieldName(IPayrollAlias.MUTUA_MUTUAS_ID_FECINI),searchFecini);
			}
			if(searchFecfin != null){
			
				getCriteria().addEqualExpression(getFieldName(IPayrollAlias.MUTUA_MUTUAS_FECFIN), searchFecfin);
			}
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		}
		searchFecini=null;
		searchFecfin=null;
		
		super.onSearch(event);
	}
	




}
