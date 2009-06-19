package com.code.aon.ui.payroll.controller;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.payroll.auxiliares.convenios.Convenio;
import com.code.aon.payroll.dao.IPayrollAlias;
import com.code.aon.payroll.enumeration.EnvioSS2;
import com.code.aon.payroll.enumeration.IndRegimen;
import com.code.aon.payroll.enumeration.Modpago;
import com.code.aon.payroll.enumeration.Tiponomina;
import com.code.aon.payroll.principales.empresa.Actividad;
import com.code.aon.payroll.principales.empresa.Empresa;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.form.FormUtil;

public class EmpreactController extends PayrollBasicController {

	private List<SelectItem> listaenvioss;
	private List<SelectItem> listamodpago;
	private List<SelectItem> listaindregimen;
	private List<SelectItem> listatiponom;
	private Actividad actividad;
	private Empresa empresa;
	private Convenio convenio;
	private Date fecini;
	private Date fecfin;
	private boolean indred;
	private boolean indmutua;
	private boolean indtc1;
	private boolean indcal;
	private boolean indnom;
	private boolean indcoste;
	private boolean flc;
	private boolean colss;
	private boolean ingespemp;
	private boolean indlogo;
	private boolean indfirma;

	/**
	 * genera un cdg siguiendo al maximo
	 */
	public void generateCdg() {
		((Actividad) getTo()).setCdg(Integer.parseInt(Utils.maxCode(
				"Actividad", "cdg")) + 1);
	}

	public List<SelectItem> getListaenvios() {
		if (listaenvioss == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot()
					.getLocale();
			listaenvioss = new LinkedList<SelectItem>();
			for (EnvioSS2 e : EnvioSS2.values()) {
				String name = e.getName(locale);
				SelectItem item = new SelectItem(e, name);
				listaenvioss.add(item);
			}
		}
		return listaenvioss;
	}

	public List<SelectItem> getListamodpagos() {
		if (listamodpago == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot()
					.getLocale();
			listamodpago = new LinkedList<SelectItem>();
			for (Modpago e : Modpago.values()) {
				String name = e.getName(locale);
				SelectItem item = new SelectItem(e, name);
				listamodpago.add(item);
			}
		}
		return listamodpago;
	}

	public List<SelectItem> getListaindregs() {
		if (listaindregimen == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot()
					.getLocale();
			listaindregimen = new LinkedList<SelectItem>();
			for (IndRegimen e : IndRegimen.values()) {
				String name = e.getName(locale);
				SelectItem item = new SelectItem(e, name);
				listaindregimen.add(item);
			}
		}
		return listaindregimen;
	}

	public List<SelectItem> getListatiposnom() {
		if (listatiponom == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot()
					.getLocale();
			listatiponom = new LinkedList<SelectItem>();
			for (Tiponomina e : Tiponomina.values()) {
				String name = e.getName(locale);
				SelectItem item = new SelectItem(e, name);
				listatiponom.add(item);
			}
		}
		return listatiponom;
	}

	@Override
	public void onEditSearch(ActionEvent arg0) {
		super.onEditSearch(arg0);

		setActividad(new Actividad());
		setEmpresa(new Empresa());
		setConvenio(new Convenio());
	}

	@Override
	public void onSearch(ActionEvent event) {

		try {
			if (empresa.getCdg() != null) {
				getCriteria().addEqualExpression(
						getFieldName(IPayrollAlias.ACTIVIDAD_EMPRESA_CDG),
						getEmpresa().getCdg());
			}
			if (actividad.getCdg() != null) {
				getCriteria().addEqualExpression(
						getFieldName(IPayrollAlias.ACTIVIDAD_CDG),
						getActividad().getCdg());
			}

			if ((convenio.getCdg() != null)
					&& (!StringUtils.isEmpty(convenio.getCdg()))) {
				getCriteria().addEqualExpression(
						getFieldName(IPayrollAlias.ACTIVIDAD_CONVENIO_CDG),
						getConvenio().getCdg());
			}

			if (fecini != null) {
				getCriteria().addEqualExpression(
						getFieldName(IPayrollAlias.ACTIVIDAD_FECINI),
						getFecini());
			}
			if (fecfin != null) {
				getCriteria().addEqualExpression(
						getFieldName(IPayrollAlias.ACTIVIDAD_FECFIN),
						getFecfin());
			}

			if (indred) {

				getCriteria().addEqualExpression(
						getFieldName(IPayrollAlias.ACTIVIDAD_INDRED), true);
			}

			if (indmutua) {

				getCriteria().addEqualExpression(
						getFieldName(IPayrollAlias.ACTIVIDAD_INDMUTUA), true);
			}
			if (indtc1) {

				getCriteria().addEqualExpression(
						getFieldName(IPayrollAlias.ACTIVIDAD_INDTC1), true);
			}
			if (indcal) {

				getCriteria().addEqualExpression(
						getFieldName(IPayrollAlias.ACTIVIDAD_INDCAL), "S");
			}
			if (indnom) {

				getCriteria().addEqualExpression(
						getFieldName(IPayrollAlias.ACTIVIDAD_INDNOM), "S");
			}
			if (indcoste) {

				getCriteria().addEqualExpression(
						getFieldName(IPayrollAlias.ACTIVIDAD_INDCOSTE), "S");
			}
			if (flc) {

				getCriteria().addEqualExpression(
						getFieldName(IPayrollAlias.ACTIVIDAD_FLC), true);
			}
			if (colss) {

				getCriteria().addEqualExpression(
						getFieldName(IPayrollAlias.ACTIVIDAD_COLSS), true);
			}
			if (ingespemp) {

				getCriteria().addEqualExpression(
						getFieldName(IPayrollAlias.ACTIVIDAD_INGESPEMP), true);
			}
			if (indfirma) {

				getCriteria().addEqualExpression(
						getFieldName(IPayrollAlias.ACTIVIDAD_INDFIRMA), true);
			}

			if (indlogo) {

				getCriteria().addEqualExpression(
						getFieldName(IPayrollAlias.ACTIVIDAD_INDRED), true);
			}

		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		indred = false;
		indmutua = false;
		indtc1 = false;
		indcal = false;
		indnom = false;
		indcoste = false;
		flc = false;
		colss = false;
		ingespemp = false;
		indlogo = false;
		indfirma = false;

		super.onSearch(event);
	}

	public List<SelectItem> getListaenvioss() {
		return listaenvioss;
	}

	public void setListaenvioss(List<SelectItem> listaenvioss) {
		this.listaenvioss = listaenvioss;
	}

	public List<SelectItem> getListamodpago() {
		return listamodpago;
	}

	public void setListamodpago(List<SelectItem> listamodpago) {
		this.listamodpago = listamodpago;
	}

	public List<SelectItem> getListaindregimen() {
		return listaindregimen;
	}

	public void setListaindregimen(List<SelectItem> listaindregimen) {
		this.listaindregimen = listaindregimen;
	}

	public List<SelectItem> getListatiponom() {
		return listatiponom;
	}

	public void setListatiponom(List<SelectItem> listatiponom) {
		this.listatiponom = listatiponom;
	}

	public Actividad getActividad() {
		return actividad;
	}

	public void setActividad(Actividad actividad) {
		this.actividad = actividad;
	}

	public Empresa getEmpresa() {
		return empresa;
	}

	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}

	public Date getFecini() {
		return fecini;
	}

	public void setFecini(Date fecini) {
		this.fecini = fecini;
	}

	public Date getFecfin() {
		return fecfin;
	}

	public void setFecfin(Date fecfin) {
		this.fecfin = fecfin;
	}

	public Convenio getConvenio() {
		return convenio;
	}

	public void setConvenio(Convenio convenio) {
		this.convenio = convenio;
	}

	public boolean getIndred() {
		return indred;
	}

	public void setIndred(boolean indred) {
		this.indred = indred;
	}

	public boolean getIndmutua() {
		return indmutua;
	}

	public void setIndmutua(boolean indmutua) {
		this.indmutua = indmutua;
	}

	public boolean getIndtc1() {
		return indtc1;
	}

	public void setIndtc1(boolean indtc1) {
		this.indtc1 = indtc1;
	}

	public boolean getIndcal() {
		return indcal;
	}

	public void setIndcal(boolean indcal) {
		this.indcal = indcal;
	}

	public boolean getIndnom() {
		return indnom;
	}

	public void setIndnom(boolean indnom) {
		this.indnom = indnom;
	}

	public boolean getIndcoste() {
		return indcoste;
	}

	public void setIndcoste(boolean indcoste) {
		this.indcoste = indcoste;
	}

	public boolean getFlc() {
		return flc;
	}

	public void setFlc(boolean flc) {
		this.flc = flc;
	}

	public boolean getColss() {
		return colss;
	}

	public void setColss(boolean colss) {
		this.colss = colss;
	}

	public boolean getIngespemp() {
		return ingespemp;
	}

	public void setIngespemp(boolean ingespemp) {
		this.ingespemp = ingespemp;
	}

	public boolean getIndlogo() {
		return indlogo;
	}

	public void setIndlogo(boolean indlogo) {
		this.indlogo = indlogo;
	}

	public boolean getIndfirma() {
		return indfirma;
	}

	public void setIndfirma(boolean indfirma) {
		this.indfirma = indfirma;
	}

	public void onEmpresaChange( LookupChangeEvent event ) {
		
		
	 if( event.getNewValue()!= null){
     int i=  ((Empresa)event.getNewValue()).getCdg();
     ((Empresa)(FormUtil.getController(IPayrollConstants.EMPRESA_CONTROLLER_NAME)).getTo()).setCdg(((Empresa)event.getNewValue()).getCdg());	
		 
	 }
	}
}
