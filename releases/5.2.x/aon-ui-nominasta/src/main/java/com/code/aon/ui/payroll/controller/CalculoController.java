package com.code.aon.ui.payroll.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.payroll.dao.IPayrollAlias;
import com.code.aon.payroll.enumeration.Indirpf;
import com.code.aon.payroll.principales.persona.Trabajador;
import com.code.aon.payroll.resultados.irpf.Calculo;

public class CalculoController extends PayrollBasicController {

	private List<SelectItem> indicadores;
	private Trabajador emprper;

	public List<SelectItem> getListaIndicadoresIrpf() {
		if(indicadores==null){
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			indicadores = new LinkedList<SelectItem>();
			for (Indirpf indi : Indirpf.values()) {
				String name = indi.getName( locale );
				SelectItem item = new SelectItem( indi, name );
				indicadores.add(item);
			}
		}
		return indicadores;
	}
	
	

	@Override
	   public void onEditSearch(ActionEvent arg0) {
		
		setEmprper( new Trabajador() );
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
		
		try {
			if( (emprper != null) && (emprper.getCdg()!=null) ) {
				getCriteria().addEqualExpression(getFieldName(IPayrollAlias.CALCULO_EMPRPER_CDG), getEmprper().getCdg());
			}
			
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		super.onSearch(event);
	}
	
	/**
	 * Genera un numero autonumerico para el codigo de la paga extra
	 * @param event
	 */
	public void generarNumero(ActionEvent event) {

		Calculo to = (Calculo) getTo();
		 
		//String where = "anio= "+to.getId().getAnio()+" and mes= "+to.getId().getMes()+" and dia= "+to.getId().getDia();
		//String num = Utils.maxCode("Calculo", "cdg",where);
		((Calculo) getTo()).getId().setCdg(((Calculo) getTo()).getEmprper().getCdg());
		
	}



	public Trabajador getEmprper() {
		return emprper;
	}



	public void setEmprper(Trabajador emprper) {
		this.emprper = emprper;
	}



}
