package com.code.aon.ui.payroll.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import com.code.aon.payroll.auxiliares.convenios.Complemento;
import com.code.aon.payroll.avanzadas.hojastrabajo.Httcomplemento;
import com.code.aon.payroll.avanzadas.hojastrabajo.Httrabajador;
import com.code.aon.payroll.enumeration.PagaExtra;
import com.code.aon.payroll.enumeration.TipoAviso;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.LinesController;


public class HttcomplementoController extends LinesController {
	

	private Complemento complemento;
	private List<SelectItem> pagas;
	
	public List<SelectItem> getListaPagaExtra() {
		if(pagas==null){
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			pagas = new LinkedList<SelectItem>();
			for (PagaExtra paga : PagaExtra.values()) {
				String name = paga.getName( locale );
				SelectItem item = new SelectItem( paga, name );
				pagas.add(item);
			}
		}
		return pagas;
	}
	

	public Complemento getComplemento() {
		return complemento;
	}

	public void setComplemento(Complemento complemento) {
		this.complemento = complemento;
	}
	
	/**
	 * genera un cdg siguiendo al maximo 
	 */
	public void generateCdg(){
		
		Integer cdg= ((Httrabajador)(FormUtil.getController(IPayrollConstants.HTTRABAJADOR_CONTROLLER_NAME)).getTo()).getCdg();
		Integer orden= Integer.parseInt(Utils.maxCode("Httcomplemento","id.orden","id.cdg="+cdg));
		
		((Httcomplemento)getTo()).getId().setCdg(((Httrabajador)(FormUtil.getController(IPayrollConstants.HTTRABAJADOR_CONTROLLER_NAME)).getTo()).getCdg());
		((Httcomplemento)getTo()).getId().setOrden(orden +1);
	}
	
    
	
}


