package com.code.aon.ui.payroll.controller;

import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.payroll.cotizacion.Base;
import com.code.aon.payroll.cotizacion.Epigrafe;
import com.code.aon.payroll.dao.IPayrollAlias;
import com.code.aon.ui.form.LinesController;

public class CategoriaLinesController extends LinesController {

	private Base base;
	private Epigrafe epigrafe;
	
	public Base getBase() {
		return base;
	}

	public void setBase(Base base) {
		this.base = base;
	}

	public Epigrafe getEpigrafe() {
		return epigrafe;
	}


	public void setEpigrafe(Epigrafe epigrafe) {
		this.epigrafe = epigrafe;
	}
	
	@Override
	public void onEditSearch(ActionEvent arg0) {
		super.onEditSearch(arg0);
		setBase( new Base() );
		setEpigrafe( new Epigrafe() );
	}

	@Override
	public void onSearch(ActionEvent event) {
		
		try {
			if( (base != null) && (! StringUtils.isEmpty(base.getCdg())) ) {
				getCriteria().addEqualExpression(getFieldName(IPayrollAlias.CATEGORIA_BASE_CDG), getBase().getCdg());
			}
			if( (epigrafe != null) && (! StringUtils.isEmpty(epigrafe.getCdg())) ) {
				getCriteria().addEqualExpression(getFieldName(IPayrollAlias.CATEGORIA_EPIGRAFE_CDG), getEpigrafe().getCdg());
			}
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		super.onSearch(event);
	}
	
	@Override
	public void onAccept(ActionEvent event) {
		System.out.println("CATEGORIA  -->  onAccept");
		
		
		if(getTo()!=null){
			System.out.println("TO con algo");
			//System.out.println(((Categoria)getTo()).getConvenio().getDescription());
			//System.out.println(((Categoria)getTo()).getNivel());
			//System.out.println(((Categoria)getTo()).getCno());
		} else 
			System.out.println("TO vacio");
	}
	
	@Override
	public void onReset(ActionEvent event) {
		// TODO Auto-generated method stub
		System.out.println("CATEGORIA  -->  onReset");
		super.onReset(event);
	}
	



}
