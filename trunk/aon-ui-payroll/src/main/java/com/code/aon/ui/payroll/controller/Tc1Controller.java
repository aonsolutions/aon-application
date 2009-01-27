package com.code.aon.ui.payroll.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.payroll.dao.IPayrollAlias;
import com.code.aon.payroll.principales.empresa.Actividad;


public class Tc1Controller extends PayrollBasicController {

	
	

	private Actividad actividad;

		
	
	
	public Actividad getActividad() {
		return actividad;
	}

	public void setActividad(Actividad actividad) {
		this.actividad = actividad;
	}

	@Override
	public void onEditSearch(ActionEvent arg0) {
		
		setActividad(new Actividad());
		
		super.onEditSearch(arg0);
	}
	

		
	
	
		@Override
		public void onSearch(ActionEvent event) {
			

			try {
		
			
			if ((actividad!=null) && (actividad.getCdg() != null)) {
				getCriteria().addEqualExpression(
						getFieldName(IPayrollAlias.TC1_CODACT_CDG),
						getActividad().getCdg());
			}
			
			
			    

			} catch (ManagerBeanException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
 
			
		
			
			super.onSearch(event);
		}


	
	
}
