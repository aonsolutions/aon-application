package com.code.aon.ui.payroll.controller;

import java.util.Date;
import java.util.List;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.payroll.dao.IPayrollAlias;

public class VariableController extends PayrollBasicController {

	
	private List<SelectItem> prorateos;


	
	


	
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
		System.out.println("------------"+searchFecini);
		System.out.println("------------"+searchFecfin);
		try {
			if(searchFecini != null){
				getCriteria().addEqualExpression(getFieldName(IPayrollAlias.VARIABLE_LINVARIABLE_ID_FECINI), searchFecini);
			}
			if(searchFecfin != null){
			
				getCriteria().addEqualExpression(getFieldName(IPayrollAlias.VARIABLE_LINVARIABLE_FECFIN), searchFecfin);
			}
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		searchFecini=null;
		searchFecfin=null;
		
		super.onSearch(event);
	}
	






}
