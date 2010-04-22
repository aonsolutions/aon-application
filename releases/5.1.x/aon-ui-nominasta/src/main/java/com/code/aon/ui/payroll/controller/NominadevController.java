package com.code.aon.ui.payroll.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import com.code.aon.payroll.auxiliares.convenios.Complemento;
import com.code.aon.payroll.enumeration.FijoVariable;
import com.code.aon.payroll.enumeration.IndiceComplemento;
import com.code.aon.payroll.enumeration.Retribuciones;
import com.code.aon.payroll.enumeration.TipoComplemento;
import com.code.aon.payroll.resultados.nomina.Nomina;
import com.code.aon.payroll.resultados.nomina.Nominadev;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.LinesController;

public class NominadevController extends LinesController {


	

	
	private List<SelectItem> tipcom;

	public List<SelectItem> getTipcom() {
		if(tipcom==null){
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			tipcom = new LinkedList<SelectItem>();
			for (TipoComplemento p : TipoComplemento.values()) {
				String name = p.getName( locale );
				SelectItem item = new SelectItem( p, name );
				tipcom.add(item);
			}
		}
		return tipcom;
	}
	private List<SelectItem> fijovar;

	public List<SelectItem> getFijovar() {
		if(fijovar==null){
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			fijovar = new LinkedList<SelectItem>();
			for (FijoVariable p : FijoVariable.values()) {
				String name = p.getName( locale );
				SelectItem item = new SelectItem( p, name );
				fijovar.add(item);
			}
		}
		return fijovar;
	}
	
	
	private List<SelectItem> dinesp;

	public List<SelectItem> getDinesp() {
		if(dinesp==null){
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			dinesp = new LinkedList<SelectItem>();
			for (Retribuciones p : Retribuciones.values()) {
				String name = p.getName( locale );
				SelectItem item = new SelectItem( p, name );
				dinesp.add(item);
			}
		}
		return dinesp;
	}
	
	
	private List<SelectItem> indicecom;

	public List<SelectItem> getIndicescom() {
		if(indicecom==null){
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			indicecom = new LinkedList<SelectItem>();
			for (IndiceComplemento p : IndiceComplemento.values()) {
				String name = p.getName( locale );
				SelectItem item = new SelectItem( p, name );
				indicecom.add(item);
			}
		}
		return indicecom;
	}
	
	
	
	
	
	Complemento complemento;
    
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
		
		
		Integer cdg= ((Nomina)(FormUtil.getController(IPayrollConstants.NOMINA_CONTROLLER_NAME)).getTo()).getCdg();

		((Nominadev)getTo()).getId().setOrden((Integer.parseInt(Utils.maxCode("Nominadev", "id.orden", "id.cdg="+cdg))+1));
		((Nominadev)getTo()).getId().setCdg(cdg);
	
	}
	
}
