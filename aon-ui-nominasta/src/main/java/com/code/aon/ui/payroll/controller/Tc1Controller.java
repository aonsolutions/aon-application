package com.code.aon.ui.payroll.controller;

import java.sql.Date;
import javax.faces.event.ActionEvent;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.payroll.dao.IPayrollAlias;
import com.code.aon.payroll.enumeration.Tipccc;
import com.code.aon.payroll.principales.empresa.Actividad;
import com.code.aon.payroll.principales.personas.Persona;
import com.code.aon.payroll.resultados.seguros.Tc1;
import com.code.aon.payroll.resultados.seguros.Tc2;


public class Tc1Controller extends PayrollBasicController {

	public void generateCdg(){
		((Tc1)getTo()).setCdg(Integer.parseInt(Utils.maxCode("Tc1", "cdg"))+1);
		
	
		Date  d= new Date(1,1,2009);
		((Tc1)getTo()).setFecmod(d);
		((Tc1)getTo()).setHormod(d);
		((Tc1)getTo()).setFecnew(d);	
		((Tc1)getTo()).setHornew(d);	
		
		 Persona p= new Persona();
		 p.setCdg(1);
	    ((Tc1)getTo()).setCodper(p);
	    
		((Tc1)getTo()).setCodccc(Tipccc.TIP2);
		
		Tc2 t= new Tc2();
		t.setCdg(4);
		((Tc1)getTo()).setCodtc2(t);
		
		

	}

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
