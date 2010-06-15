package com.code.aon.ui.payroll.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import com.code.aon.payroll.avanzadas.hojastrabajo.Httaviso;
import com.code.aon.payroll.avanzadas.hojastrabajo.Httrabajador;
import com.code.aon.payroll.enumeration.TipoAviso;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.LinesController;


public class HttavisoController extends LinesController {
	
	/**
	 * genera un cdg siguiendo al maximo 
	 */
	public void generateCdg(){
		
		Integer cdg= ((Httrabajador)(FormUtil.getController(IPayrollConstants.HTTRABAJADOR_CONTROLLER_NAME)).getTo()).getCdg();
		Integer orden= Integer.parseInt(Utils.maxCode("Httaviso","id.orden","id.cdg="+cdg));
		
		((Httaviso)getTo()).getId().setCdg(((Httrabajador)(FormUtil.getController(IPayrollConstants.HTTRABAJADOR_CONTROLLER_NAME)).getTo()).getCdg());
		((Httaviso)getTo()).getId().setOrden(orden +1);
	}
	
    
	private List<SelectItem> avisos;
	
	public List<SelectItem> getListaavisos() {
		if(avisos==null){
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			avisos = new LinkedList<SelectItem>();
			for (TipoAviso paga : TipoAviso.values()) {
				String name = paga.getName( locale );
				SelectItem item = new SelectItem( paga, name );
				avisos.add(item);
			}
		}
		return avisos;
	}

}


