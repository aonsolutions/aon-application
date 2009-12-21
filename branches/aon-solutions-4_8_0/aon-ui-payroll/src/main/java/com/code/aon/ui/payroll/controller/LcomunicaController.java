package com.code.aon.ui.payroll.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import com.code.aon.payroll.avanzadas.simulacion.Costes;
import com.code.aon.payroll.avanzadas.simulacion.Lcomunica;
import com.code.aon.payroll.enumeration.Ascdes;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.LinesController;

public class LcomunicaController extends LinesController {
	
	
	private List<SelectItem> desasc;

	public List<SelectItem> getListadesc() {
		if (desasc == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot()
					.getLocale();
			desasc = new LinkedList<SelectItem>();
			for (Ascdes e : Ascdes.values()) {
				String name = e.getName(locale);
				SelectItem item = new SelectItem(e, name);
				desasc.add(item);
			}
		}
		return desasc;
	}	
		
	
	
	
    
public void generateCdg(){
		
		Integer cdg= ((Costes)(FormUtil.getController(IPayrollConstants.COSTES_CONTROLLER_NAME)).getTo()).getId().getCdg();
		Integer numero= Integer.parseInt(Utils.maxCode("Lcomunica","id.orden"));
		
		((Lcomunica)getTo()).getId().setCdg(((Costes)(FormUtil.getController(IPayrollConstants.COSTES_CONTROLLER_NAME)).getTo()).getId().getCdg());
		((Lcomunica)getTo()).getId().setOrden(numero +1);
		((Lcomunica)getTo()).getId().setNumero(1);
	}
	
	
	
}
