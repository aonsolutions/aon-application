package com.code.aon.ui.payroll.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.payroll.dao.IPayrollAlias;

public class CotizacionOcupacionController extends PayrollBasicController {

	private boolean searchExclusivo;

	// añade el valor de los checkbox al criteria para realizar busquedas
	@Override
	public void onSearch(ActionEvent event) {

		try {
			if (searchExclusivo)
				getCriteria()
						.addEqualExpression(getFieldName(IPayrollAlias.OCUPACION_MAESTRO_EXCLUSIVO), "S");
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		searchExclusivo = false;

		super.onSearch(event);

	}

	public boolean getSearchExclusivo() {
		return searchExclusivo;
	}

	public void setSearchExclusivo(boolean searchExclusivo) {
		this.searchExclusivo = searchExclusivo;
	}

}
