package com.code.aon.ui.payroll.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.payroll.auxiliares.convenios.Convenio;
import com.code.aon.payroll.dao.IPayrollAlias;
import com.code.aon.payroll.enumeration.IndiceAgrario;
import com.code.aon.payroll.enumeration.IndiceGrupo;
import com.code.aon.payroll.enumeration.Prorateo;
import com.code.aon.payroll.enumeration.RelacionLaboral;
import com.code.aon.payroll.principales.Domicilio;
import com.code.aon.payroll.principales.empresa.Actividad;
import com.code.aon.payroll.principales.empresa.Emprccos;
import com.code.aon.payroll.principales.empresa.Empresa;
import com.code.aon.ui.form.LinesController;

public class TrabajoController extends LinesController {
	
	private static final Logger LOGGER = Logger.getLogger(CotizacionBonificacionController.class.getName());

	private List<SelectItem> indagrario;
	private List<SelectItem> indgrupo;
	
	private Empresa empresa;
	private Actividad actividad;
	private Domicilio domicilio;
	private Emprccos emprccos;
	private Convenio convenio;
		
	public List<SelectItem> getIndagrario() {
		return indagrario;
	}

	public void setIndagrario(List<SelectItem> indagrario) {
		this.indagrario = indagrario;
	}

	public List<SelectItem> getIndgrupo() {
		return indgrupo;
	}

	public void setIndgrupo(List<SelectItem> indgrupo) {
		this.indgrupo = indgrupo;
	}
	
	public List<SelectItem> getListaIndiceAgrario() {
		if(indagrario==null){
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			indagrario = new LinkedList<SelectItem>();
			for (IndiceAgrario e : IndiceAgrario.values()) {
				String name = e.getName( locale );
				SelectItem item = new SelectItem( e, name );
				indagrario.add(item);
			}
		}
		return indagrario;
	}

	public List<SelectItem> getListaIndiceGrupo() {
		if(indgrupo==null){
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			indgrupo = new LinkedList<SelectItem>();
			for (IndiceGrupo e : IndiceGrupo.values()) {
				String name = e.getName( locale );
				SelectItem item = new SelectItem( e, name );
				indgrupo.add(item);
			}
		}
		return indgrupo;
	}
	
	@Override
	public void onEditSearch(ActionEvent arg0) {

		
		
		setEmpresa(new Empresa());
		setActividad(new Actividad());
		setDomicilio(new Domicilio());
		setEmprccos(new Emprccos());

		super.onEditSearch(arg0);
	}
	
	@Override
	public void onSearch(ActionEvent event) {
		
		try {
			//Búsqueda por campos LookUp
			
			if (empresa!=null && (empresa.getCdg() != null)) {
				getCriteria().addEqualExpression(getFieldName(IPayrollAlias.TRABAJADOR_EMPRESA_CDG), empresa.getCdg());
			}
			if (actividad!=null && (actividad.getCdg() != null)) {
				getCriteria().addEqualExpression(getFieldName(IPayrollAlias.TRABAJADOR_ACTIVIDAD_CDG), actividad.getCdg());
			}
			if (domicilio!=null && (domicilio.getCdg() != null)) {
				getCriteria().addEqualExpression(getFieldName(IPayrollAlias.TRABAJADOR_DOMICILIO_CDG), domicilio.getCdg());
			}
			if (emprccos!=null && (emprccos.getCdg() != null)) {
				getCriteria().addEqualExpression(getFieldName(IPayrollAlias.TRABAJADOR_EMPRCCOS_CDG), emprccos.getCdg());
			}
			
		} catch (ManagerBeanException e) {
			LOGGER.log( Level.SEVERE, e.getMessage(), e );
			e.printStackTrace();
		}
		super.onSearch(event);
	}

	public Empresa getEmpresa() {
		return empresa;
	}

	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}

	public Actividad getActividad() {
		return actividad;
	}

	public void setActividad(Actividad actividad) {
		this.actividad = actividad;
	}

	public Domicilio getDomicilio() {
		return domicilio;
	}

	public void setDomicilio(Domicilio domicilio) {
		this.domicilio = domicilio;
	}

	public Emprccos getEmprccos() {
		return emprccos;
	}

	public void setEmprccos(Emprccos emprccos) {
		this.emprccos = emprccos;
	}
	
	public Convenio getConvenio() {
		return convenio;
	}

	public void setConvenio(Convenio convenio) {
		this.convenio = convenio;
	}
	
	
	
	private String tabName;

	/**
	 * Devuelve el tab seleccionado.
	 * El objetvo es mantener la pestaña activa entre navegaciones.
	 * 
	 * @return
	 */
	public String getTabName() {
		return tabName;
	}

	public void setTabName(String tabName) {
		this.tabName = tabName;
	}
	
	public void changeTabValue(ValueChangeEvent event){
		setTabName(event.getNewValue().toString());
	}
	
	
	private List<SelectItem> relacion;
	private List<SelectItem> procot;
	private List<SelectItem> proret;

	/**
	 * Recupera los tipo de relacion laboral.
	 * 
	 * @return
	 */
	public List<SelectItem> getRelacion() {
		if ( relacion == null ) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			relacion = new LinkedList<SelectItem>();
			for (RelacionLaboral rl : RelacionLaboral.values()) {
				String name = rl.getName( locale );
				SelectItem item = new SelectItem( rl, name );
				relacion.add(item);
			}
		}
		return relacion;
	}
	
	/**
	 * Recupera los tipo de prorrateo de cotizacion.
	 * 
	 * @return
	 */
	public List<SelectItem> getProcot() {
		if ( procot == null ) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			procot = new LinkedList<SelectItem>();
			for (Prorateo p : Prorateo.values()) {
				String name = p.getName( locale );
				SelectItem item = new SelectItem( p, name );
				procot.add(item);
			}
		}
		return procot;
	}
	
	/**
	 * Recupera los tipo de prorrateo de retribucion.
	 * 
	 * @return
	 */
	public List<SelectItem> getProret() {
		if ( proret == null ) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			proret = new LinkedList<SelectItem>();
			for (Prorateo p : Prorateo.values()) {
				String name = p.getName( locale );
				SelectItem item = new SelectItem( p, name );
				proret.add(item);
			}
		}
		return proret;
	}
	


	
}
