package com.code.aon.ui.payroll.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import com.code.aon.payroll.enumeration.FijoVariable;
import com.code.aon.ui.form.LinesController;

public class LinCalculoController extends LinesController {

	private List<SelectItem> fijovar;

	public List<SelectItem> getListaFijoVariable() {
		if(fijovar==null){
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			fijovar = new LinkedList<SelectItem>();
			for (FijoVariable fv : FijoVariable.values()) {
				String name = fv.getName( locale );
				SelectItem item = new SelectItem( fv, name );
				fijovar.add(item);
			}
		}
		return fijovar;
	}
	
	

	@Override
	   public void onEditSearch(ActionEvent arg0) {
		   super.onEditSearch(arg0);
	
	}

	@Override
	public void onSearch(ActionEvent event) {
		
		/*

		try {
			if   (empresa.getCdg() != null)  {
				getCriteria().addEqualExpression(getFieldName(IPayrollAlias.OTRPERC_EMPRESA_CDG), getEmpresa().getCdg());
			}
			if   (persona.getCdg() != null) {
				getCriteria().addEqualExpression(getFieldName(IPayrollAlias.OTRPERC_PERSONA_CDG), getPersona().getCdg());
			}
		     
		    if    (fecha != null){
					getCriteria().addEqualExpression(getFieldName(IPayrollAlias.OTRPERC_FECHA), getFecha());
				}
		    
		
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		
		super.onSearch(event);
	}
	
	public void setDefaultFields(){		

		/*
		Otrperc c = (Otrperc)getTo();		
		c.setClave(Claveper.CLAVE7);
		c.setNatret(Retribuciones.DINERARIA);
		c.setIngreso(Ingreso.ING1);
		*/
	
		

	}
	
	

}
