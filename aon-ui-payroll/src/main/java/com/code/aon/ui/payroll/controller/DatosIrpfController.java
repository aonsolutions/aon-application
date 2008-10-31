package com.code.aon.ui.payroll.controller;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.payroll.cotizacion.PorcentajeMaestro;
import com.code.aon.payroll.dao.IPayrollAlias;
import com.code.aon.payroll.enumeration.Desempleado;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionUtilities;

public class DatosIrpfController extends PayrollBasicController {

	
	private boolean searchPorcentaje;
	
	
	//añade el valor de los checkbox al criteria para realizar busquedas
	@Override
	public void onSearch(ActionEvent event) {
		
		try {
			if(searchPorcentaje)
				getCriteria().addEqualExpression(getFieldName(IPayrollAlias.ELEMIRPF_PORCENTAJE), "S");
				
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		searchPorcentaje=false;
		
		
		super.onSearch(event);
	}

	public boolean getSearchPorcentaje() {
		return searchPorcentaje;
	}

	public void setSearchPorcentaje(boolean searchPorcentaje) {
		this.searchPorcentaje = searchPorcentaje;
	}



}
