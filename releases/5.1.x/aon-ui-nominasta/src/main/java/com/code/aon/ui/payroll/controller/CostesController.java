package com.code.aon.ui.payroll.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import com.code.aon.payroll.avanzadas.simulacion.Costes;
import com.code.aon.payroll.cotizacion.Base;
import com.code.aon.payroll.cotizacion.Epigrafe;
import com.code.aon.payroll.cotizacion.PorcentajeMaestro;
import com.code.aon.payroll.enumeration.Minusvalia;
import com.code.aon.payroll.enumeration.Prorateo;
import com.code.aon.payroll.enumeration.Sitfami;
import com.code.aon.payroll.enumeration.Timecont;
import com.code.aon.payroll.principales.persona.Trabajador;

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
	
	private Trabajador trabajador;
	private Base basecoti;
    private Epigrafe epigrafe;
    private PorcentajeMaestro porcoti;
	
	public Base getBasecoti() {
		return basecoti;
	}

	public void setBasecoti(Base basecoti) {
		this.basecoti = basecoti;
	}

	public Epigrafe getEpigrafe() {
		return epigrafe;
	}

	public void setEpigrafe(Epigrafe epigrafe) {
		this.epigrafe = epigrafe;
	}

	public PorcentajeMaestro getPorcoti() {
		return porcoti;
	}

	public void setPorcoti(PorcentajeMaestro porcoti) {
		this.porcoti = porcoti;
	}

	public Trabajador getTrabajador() {
		return trabajador;
	}

	public void setTrabajador(Trabajador trabajador) {
		this.trabajador = trabajador;
	}
	
	@Override
	public void onEditSearch(ActionEvent arg0) {
		
		setTrabajador(new Trabajador());
		setPorcoti(new PorcentajeMaestro());
		setBasecoti(new Base());
		setEpigrafe(new Epigrafe());
		
		super.onEditSearch(arg0);
	}
	

	public void generateCdg(){
		
		Integer cdg= Integer.parseInt(Utils.maxCode("Costes","id.cdg"));
		
		((Costes)getTo()).getId().setNumero(1);
		((Costes)getTo()).getId().setCdg(cdg +1);
	}
	
	/*
		@Override
		public void onSearch(ActionEvent event) {
			

			try {
		
			
			if ((trabajador!=null) && (trabajador.getCdg() != null)) {
				getCriteria().addEqualExpression(
						getFieldName(IPayrollAlias.COSTES_CODEMP_CDG),
						getTrabajador().getCdg());
			}
			
			if ((epigrafe!=null) && (epigrafe.getCdg() != null)) {
				getCriteria().addEqualExpression(
						getFieldName(IPayrollAlias.COSTES_EPIGRAFE_CDG),
						getEpigrafe().getCdg());
			}
			if ((porcoti!=null) && (porcoti.getCdg() != null)) {
				getCriteria().addEqualExpression(
						getFieldName(IPayrollAlias.COSTES_PORCOTI_CDG),
						getPorcoti().getCdg());
			}
			
			if ((basecoti!=null) && (basecoti.getCdg() != null)) {
				getCriteria().addEqualExpression(
						getFieldName(IPayrollAlias.COSTES_BASECOTI_CDG),
						getBasecoti().getCdg());
			}
			
			
			    

			} catch (ManagerBeanException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
 
			
		
			
			super.onSearch(event);
		}
	*/
	

	
}
