package com.code.aon.ui.payroll.controller;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.ActionEvent;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.payroll.dao.IPayrollAlias;

public class DatosIrpfController extends PayrollBasicController {
	
	private static final Logger LOGGER = Logger.getLogger(DatosIrpfController.class.getName());
	
	private boolean searchPorcentaje;
	private Date searchFecfin;
	private Date searchFecini;
	
	
	//añade el valor de los checkbox al criteria para realizar busquedas
	@Override
	public void onSearch(ActionEvent event) {
		
		try {
			if(searchPorcentaje){
				getCriteria().addEqualExpression(getFieldName(IPayrollAlias.ELEMIRPF_PORCENTAJE), "S");
			}			
			
			/*if(searchFecini != null){
				getCriteria().addEqualExpression(getFieldName(IPayrollAlias.ELEMIRPF_ELEMIRPFS_ID_FECINI), searchFecini);
			}
			if(searchFecfin != null){
				getCriteria().addEqualExpression(getFieldName(IPayrollAlias.ELEMIRPF_ELEMIRPFS_FECFIN), searchFecfin);
			}*/
				
		} catch (ManagerBeanException e) {
			LOGGER.log( Level.SEVERE, e.getMessage(), e );
		}
		
		searchPorcentaje=false;
		searchFecini=null;
		searchFecfin=null;
		
		
		super.onSearch(event);
	}

	public boolean getSearchPorcentaje() {
		return searchPorcentaje;
	}

	public void setSearchPorcentaje(boolean searchPorcentaje) {
		this.searchPorcentaje = searchPorcentaje;
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
	
	
}
