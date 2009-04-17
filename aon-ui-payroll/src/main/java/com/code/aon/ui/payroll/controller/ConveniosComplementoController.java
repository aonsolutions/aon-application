package com.code.aon.ui.payroll.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.payroll.auxiliares.convenios.Complemento;
import com.code.aon.payroll.auxiliares.convenios.Convenio;
import com.code.aon.payroll.dao.IPayrollAlias;
import com.code.aon.payroll.enumeration.TipoComplemento;
import com.code.aon.payroll.enumeration.TipoCotizaciones;
import com.code.aon.payroll.enumeration.Retribuciones;
import com.code.aon.payroll.enumeration.FijoVariable;
import com.code.aon.payroll.enumeration.IndiceComplemento;


public class ConveniosComplementoController extends PayrollBasicController {

	private List<SelectItem> cotizaciones;
	private List<SelectItem> complementos;
	private List<SelectItem> retribuciones;
	private List<SelectItem> fijoVariable;
	private List<SelectItem> indComp;
	
	private ConveniosComplementoController complementoPrint;

	/**
	 * Recupera los tipos de cotizaciones 
	 * 
	 * @return
	 */
	public List<SelectItem> getListaCotizaciones() {
		return cotizaciones;
	}	

	/**
	 * Recupera los tipos de complementos 
	 * 
	 * @return
	 */
	public List<SelectItem> getListaComplementos() {
		return complementos;
	}
	
	@SuppressWarnings("unchecked")
	public void refreshCotizaciones() throws ManagerBeanException {
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		cotizaciones = new LinkedList<SelectItem>();
		for (TipoCotizaciones cotizacion : TipoCotizaciones.values()) {
			String name = cotizacion.getName( locale );
			SelectItem item = new SelectItem( cotizacion, name );
			cotizaciones.add(item);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void refreshComplementos() throws ManagerBeanException {
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		complementos = new LinkedList<SelectItem>();
		for (TipoComplemento complemento : TipoComplemento.values()) {
			String name = complemento.getName( locale );
			SelectItem item = new SelectItem( complemento, name );
			complementos.add(item);
		}
	}
	
	/**
	 * Recupera los tipos de retribuciones 
	 * 
	 * @return
	 */
	public List<SelectItem> getListaRetribuciones() {
		if(retribuciones==null){
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			retribuciones = new LinkedList<SelectItem>();
			for (Retribuciones retribucion : Retribuciones.values()) {
				String name = retribucion.getName( locale );
				SelectItem item = new SelectItem( retribucion, name );
				retribuciones.add(item);
			}
		}
		return retribuciones;
	}
	
	/**
	 * Recupera el listado de fijo o variable 
	 * 
	 * @return
	 */
	public List<SelectItem> getListaFijoVariable() {
		if(fijoVariable==null){
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			fijoVariable = new LinkedList<SelectItem>();
			for (FijoVariable ret : FijoVariable.values()) {
				String name = ret.getName( locale );
				SelectItem item = new SelectItem( ret, name );
				fijoVariable.add(item);
			}
		}
		return fijoVariable;
	}
	
	/**
	 * Recupera el listado de indices de complementos 
	 * 
	 * @return
	 */
	public List<SelectItem> getListaIndiceComplemento() {
		if(indComp==null){
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			indComp = new LinkedList<SelectItem>();
			for (IndiceComplemento ic : IndiceComplemento.values()) {
				String name = ic.getName( locale );
				SelectItem item = new SelectItem( ic, name );
				indComp.add(item);
			}
		}
		return indComp;
	}

	public ConveniosComplementoController getComplementoPrint() {
		return complementoPrint;
	}

	public void setComplementoPrint(ConveniosComplementoController complementoPrint) {
		this.complementoPrint = complementoPrint;
	}
	
	@Override
	public void onSelect(ActionEvent event) {
		// TODO Auto-generated method stub
		super.onSelect(event);
		
		// guarda unicamente el TO seleccionado en el controlador print
		if(getTo()!=null){
			try {
				String id = BeanManager.getManagerBean(Complemento.class).getFieldName(IPayrollAlias.COMPLEMENTO_CDG);
				String cdg = ((Complemento)getTo()).getCdg();
				complementoPrint = new ConveniosComplementoController();
				complementoPrint = this;
				complementoPrint.clearCriteria();
				complementoPrint.getCriteria().addEqualExpression(id, cdg);
			} catch (ManagerBeanException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void onSearch(ActionEvent event) {
		// TODO Auto-generated method stub
		try {
			this.clearCriteria();
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		super.onSearch(event);
	}
	
	

}
