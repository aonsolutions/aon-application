package com.code.aon.ui.payroll.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.payroll.auxiliares.organismosyentidades.Mutua;
import com.code.aon.payroll.dao.IPayrollAlias;
import com.code.aon.payroll.enumeration.Tipccc;
import com.code.aon.payroll.principales.empresa.Actividad;
import com.code.aon.payroll.principales.empresa.Emprccc;
import com.code.aon.ui.form.LinesController;

public class EmprcccController extends LinesController {


	
	private List<SelectItem> tipccc;
	private Actividad actividad;
	private Mutua mutua;
	private boolean indss;
	
	
	
    
	/**
	 * genera un cdg siguiendo al maximo 
	 */
	public void generateCdg(){
		((Emprccc)getTo()).getId().setCdg(Integer.parseInt(Utils.maxCode("Emprccc", "cdg"))+1);
	}
	
	
	public boolean getIndss() {
		return indss;
	}


	public void setIndss(boolean indss) {
		this.indss = indss;
	}


	public List<SelectItem> getListatipos() {
		if(tipccc==null){
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			tipccc = new LinkedList<SelectItem>();
			for (Tipccc e : Tipccc.values()) {
				String name = e.getName( locale );
				SelectItem item = new SelectItem( e, name );
				tipccc.add(item);
			}
		}
		return tipccc;
	}
	
	
	@Override
	   public void onEditSearch(ActionEvent arg0) {
		   super.onEditSearch(arg0);
		   
	       setActividad( new Actividad() );
	       setMutua( new Mutua() );
	    ;
		   }
		
	
	
		@Override
		public void onSearch(ActionEvent event) {
			

			try {
			if	((mutua.getCdg() != null) &&  (!StringUtils.isEmpty(mutua.getCdg()))) {
				getCriteria().addEqualExpression(
						getFieldName(IPayrollAlias.EMPRCCC_MUTUA_CDG),
						getMutua().getCdg());
			}
			if (actividad.getCdg() != null) {
				getCriteria().addEqualExpression(
						getFieldName(IPayrollAlias.EMPRCCC_ACTIVIDAD_CDG),
						getActividad().getCdg());
			}
			
			
			if(indss){
				
				getCriteria().addEqualExpression(
						getFieldName(IPayrollAlias.EMPRCCC_INDSS), true);
			}

			

			} catch (ManagerBeanException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
	    indss = false;
	 
		
			
			super.onSearch(event);
		}


		public Actividad getActividad() {
			return actividad;
		}


		public void setActividad(Actividad actividad) {
			this.actividad = actividad;
		}


		public Mutua getMutua() {
			return mutua;
		}


		public void setMutua(Mutua mutua) {
			this.mutua = mutua;
		}


	
	
}
