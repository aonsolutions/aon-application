package com.code.aon.ui.payroll.controller;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.payroll.auxiliares.organismosyentidades.Delegacion;
import com.code.aon.payroll.dao.IPayrollAlias;
import com.code.aon.payroll.divisa.Divisa;
import com.code.aon.payroll.enumeration.Claveper;
import com.code.aon.payroll.enumeration.FijoVariable;
import com.code.aon.payroll.enumeration.IndicadorAnio;
import com.code.aon.payroll.enumeration.IndiceComplemento;
import com.code.aon.payroll.enumeration.Ingreso;
import com.code.aon.payroll.enumeration.Retribuciones;
import com.code.aon.payroll.enumeration.TipoComplemento;
import com.code.aon.payroll.enumeration.TipoCotizaciones;
import com.code.aon.payroll.enumeration.TipoProrrateo;
import com.code.aon.payroll.geograficas.Pais;
import com.code.aon.payroll.geograficas.Provincia;
import com.code.aon.payroll.principales.empresa.Empresa;
import com.code.aon.payroll.principales.personas.Persona;
import com.code.aon.payroll.tipos.Documento;
import com.code.aon.payroll.tipos.Empresario;
import com.code.aon.payroll.tipos.Tipovia;

public class PercepController extends PayrollBasicController {

	private List<SelectItem> complementos;
	private List<SelectItem> retribuciones;
	private List<SelectItem> fijoVariable;
	private List<SelectItem> indComp;

	
	
	
	public List<SelectItem> getListaComplementos() {
		return complementos;
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
}
