package com.code.aon.ui.payroll.controller;

import java.util.Date;

import javax.faces.event.ActionEvent;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.payroll.dao.IPayrollAlias;

public class CuotaRetencionAlavaController extends PayrollBasicController {

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
		System.out.println("------------"+searchFecini);
		System.out.println("------------"+searchFecfin);
		try {
			if(searchFecini != null){
				getCriteria().addEqualExpression(getFieldName(IPayrollAlias.CUOTA_RETENCION_ALAVA_ID_FECINI), searchFecini);
			}
			if(searchFecfin != null){
				//getCriteria().addEqualExpression(getFieldName(event.getComponent().getId()), searchFecfin);
				getCriteria().addEqualExpression(getFieldName(IPayrollAlias.CUOTA_RETENCION_ALAVA_FECFIN), searchFecfin);
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
