package com.code.aon.ui.payroll.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.payroll.dao.IPayrollAlias;
import com.code.aon.payroll.enumeration.Tipnomina;
import com.code.aon.payroll.principales.persona.Trabajador;
import com.code.aon.payroll.resultados.nomina.Nomina;

public class NominaController extends PayrollBasicController {

	private List<SelectItem> tipnomina;
	private Trabajador trabajador;

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
		((Nomina)getTo()).setCdg(Integer.parseInt(Utils.maxCode("Nomina", "cdg"))+1);
		((Nomina)getTo()).setOrden(0);
	}
	
	
	
	
	@Override
	public void onEditSearch(ActionEvent arg0) {
		
		setTrabajador(new Trabajador());
		
		super.onEditSearch(arg0);
	}
	

		
	
	
		@Override
		public void onSearch(ActionEvent event) {
			

			try {
		
			
			if ((trabajador!=null) && (trabajador.getCdg() != null)) {
				getCriteria().addEqualExpression(
						getFieldName(IPayrollAlias.NOMINA_TRABAJADOR_CDG),
						getTrabajador().getCdg());
			}
			
			
			    

			} catch (ManagerBeanException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
 
			
		
			
			super.onSearch(event);
		}

		public Trabajador getTrabajador() {
			return trabajador;
		}

		public void setTrabajador(Trabajador trabajador) {
			this.trabajador = trabajador;
		}

	
	
}
