package com.code.aon.ui.payroll.controller;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.payroll.auxiliares.convenios.Convenio;
import com.code.aon.payroll.auxiliares.organismosyentidades.Mutua;
import com.code.aon.payroll.auxiliares.organismosyentidades.Sucursal;
import com.code.aon.payroll.cotizacion.Linepigr;
import com.code.aon.payroll.dao.IPayrollAlias;
import com.code.aon.payroll.enumeration.EnvioSS2;
import com.code.aon.payroll.enumeration.Epigrafes;
import com.code.aon.payroll.enumeration.IndRegimen;
import com.code.aon.payroll.enumeration.Minusvalia;
import com.code.aon.payroll.enumeration.Modpago;
import com.code.aon.payroll.enumeration.Prorateo;
import com.code.aon.payroll.enumeration.Sitfami;
import com.code.aon.payroll.enumeration.Timecont;
import com.code.aon.payroll.enumeration.Tipcuenta;
import com.code.aon.payroll.enumeration.Tiponomina;
import com.code.aon.payroll.geograficas.Provincia;
import com.code.aon.payroll.principales.empresa.Actividad;
import com.code.aon.payroll.principales.empresa.Emprdom;
import com.code.aon.payroll.principales.empresa.Empresa;
import com.code.aon.payroll.principales.personas.Persona;
import com.code.aon.payroll.tipos.Tipovia;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.LinesController;

public class CostesController extends PayrollBasicController	 {



	
	private List<SelectItem> timeconts;
	private List<SelectItem> situaciones;
	private List<SelectItem> minusvalias;
	private List<SelectItem> prorateos;


	public List<SelectItem> getTimeconts() {
		return timeconts;
	}

	public void setTimeconts(List<SelectItem> timeconts) {
		this.timeconts = timeconts;
	}

	public List<SelectItem> getSituaciones() {
		return situaciones;
	}

	public void setSituaciones(List<SelectItem> situaciones) {
		this.situaciones = situaciones;
	}

	public List<SelectItem> getMinusvalias() {
		return minusvalias;
	}

	public void setMinusvalias(List<SelectItem> minusvalias) {
		this.minusvalias = minusvalias;
	}

	public List<SelectItem> getProrateos() {
		return prorateos;
	}

	public void setProrateos(List<SelectItem> prorateos) {
		this.prorateos = prorateos;
	}

	public List<SelectItem> getListajornada() {
		if (timeconts == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot()
					.getLocale();
			timeconts = new LinkedList<SelectItem>();
			for (Timecont e : Timecont.values()) {
				String name = e.getName(locale);
				SelectItem item = new SelectItem(e, name);
				timeconts.add(item);
			}
		}
		return timeconts;
	}

	
	public List<SelectItem> getListasitus() {
		if (situaciones == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot()
					.getLocale();
			situaciones = new LinkedList<SelectItem>();
			for (Sitfami e : Sitfami.values()) {
				String name = e.getName(locale);
				SelectItem item = new SelectItem(e, name);
				situaciones.add(item);
			}
		}
		return situaciones;
	}	
	
	
	public List<SelectItem> getListaminus() {
		if (minusvalias == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot()
					.getLocale();
			minusvalias = new LinkedList<SelectItem>();
			for (Minusvalia e : Minusvalia.values()) {
				String name = e.getName(locale);
				SelectItem item = new SelectItem(e, name);
				minusvalias.add(item);
			}
		}
		return minusvalias;
	}	
	
	public List<SelectItem> getListadimes() {
		if (prorateos == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot()
					.getLocale();
			prorateos = new LinkedList<SelectItem>();
			for (Prorateo e : Prorateo.values()) {
				String name = e.getName(locale);
				SelectItem item = new SelectItem(e, name);
				prorateos.add(item);
			}
		}
		return prorateos;
	}	
	
}
