package com.code.aon.ui.payroll.controller;

import java.util.Date;

import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.payroll.dao.IPayrollAlias;
import com.code.aon.payroll.geograficas.Provincia;
import com.code.aon.payroll.tipos.Tipovia;

public class DelegacionController extends PayrollBasicController {

	private Provincia provincia;
	private Tipovia tipovia;
	
	public Provincia getProvincia() {
		return provincia;
	}

	public void setProvincia(Provincia idprovincia) {
		this.provincia = idprovincia;
	}

	public Tipovia getTipovia() {
		return tipovia;
	}


	public void setTipovia(Tipovia idtipovia) {
		this.tipovia = idtipovia;
	}
	
	@Override
	public void onEditSearch(ActionEvent arg0) {
		super.onEditSearch(arg0);
		setProvincia( new Provincia() );
		setTipovia( new Tipovia() );
	}

	@Override
	public void onSearch(ActionEvent event) {
		System.out.println("------------"+tipovia);
		System.out.println("------------"+provincia);
		
		try {
			if( (provincia != null) && (! StringUtils.isEmpty(provincia.getCdg())) ) {
				getCriteria().addEqualExpression(getFieldName(IPayrollAlias.DELEGACION_PROVINCIA_CDG), getProvincia().getCdg());
			}
			if( (tipovia != null) && (! StringUtils.isEmpty(tipovia.getCdg())) ) {
				getCriteria().addEqualExpression(getFieldName(IPayrollAlias.DELEGACION_TIPOVIA_CDG), getTipovia().getCdg());
			}
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		super.onSearch(event);
	}



}
