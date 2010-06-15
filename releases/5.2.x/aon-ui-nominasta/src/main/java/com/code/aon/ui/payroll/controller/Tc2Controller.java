package com.code.aon.ui.payroll.controller;

import java.sql.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.payroll.dao.IPayrollAlias;
import com.code.aon.payroll.enumeration.Tipccc;
import com.code.aon.payroll.enumeration.Tipnomina;
import com.code.aon.payroll.principales.empresa.Actividad;
import com.code.aon.payroll.resultados.seguros.Tc2;

public class Tc2Controller extends PayrollBasicController {

	
	
	private Actividad actividad;
	
	private List<SelectItem> tipnomina;

	public List<SelectItem> getTipnomina() {
		if(tipnomina==null){
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			tipnomina = new LinkedList<SelectItem>();
			for (Tipnomina p : Tipnomina.values()) {
				String name = p.getName( locale );
				SelectItem item = new SelectItem( p, name );
				tipnomina.add(item);
			}
		}
		return tipnomina;
	}
	
	/**
	 * genera un cdg siguiendo al maximo 
	 */
	public void generateCdg(){
		((Tc2)getTo()).setCdg(Integer.parseInt(Utils.maxCode("Tc2", "cdg"))+1);
		Date  d= new Date(1/1/2009);
		((Tc2)getTo()).setFecmod(d);
		((Tc2)getTo()).setHormod(d);
		((Tc2)getTo()).setFecnew(d);	
		((Tc2)getTo()).setHornew(d);
		((Tc2)getTo()).setCodccc(Tipccc.TIP2);
		
	}


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
						getFieldName(IPayrollAlias.TC2_CODACT_CDG),
						getActividad().getCdg());
			}
			
			
			    

			} catch (ManagerBeanException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
 
			
		
			
			super.onSearch(event);
		}


	
	
}
