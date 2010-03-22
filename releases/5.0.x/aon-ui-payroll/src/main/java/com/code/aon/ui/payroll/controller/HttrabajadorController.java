package com.code.aon.ui.payroll.controller;

import java.sql.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.payroll.auxiliares.contratos.ContratosInternos;
import com.code.aon.payroll.auxiliares.contratos.ContratosTc2;
import com.code.aon.payroll.auxiliares.convenios.Categoria;
import com.code.aon.payroll.auxiliares.organismosyentidades.Entidad;
import com.code.aon.payroll.auxiliares.organismosyentidades.Sucursal;
import com.code.aon.payroll.avanzadas.hojastrabajo.Httrabajador;
import com.code.aon.payroll.cotizacion.Base;
import com.code.aon.payroll.cotizacion.Epigrafe;
import com.code.aon.payroll.dao.IPayrollAlias;
import com.code.aon.payroll.enumeration.EstadoCivil;
import com.code.aon.payroll.enumeration.Prorateo;
import com.code.aon.payroll.enumeration.Timecont;
import com.code.aon.payroll.enumeration.TipIrpf;
import com.code.aon.payroll.enumeration.Tipccc;
import com.code.aon.payroll.geograficas.Pais;
import com.code.aon.payroll.geograficas.Provincia;
import com.code.aon.payroll.principales.Domicilio;
import com.code.aon.payroll.principales.empresa.Actividad;
import com.code.aon.payroll.tipos.Documento;
import com.code.aon.payroll.tipos.Tipovia;

public class HttrabajadorController extends PayrollBasicController {

		
	
	private Documento documento;
	private Actividad actividad;
	private Domicilio domicilio;
	private Pais pais;
	private Provincia provincia;
	private Provincia provincia1;
	private Tipovia tipovia;
	private Date fecnac;
	private ContratosInternos tipocont;
	private ContratosTc2 tipcotc2;
	private Entidad entidad;
	private Epigrafe epigrafe;
	private Sucursal sucursal1;
	private Base basecoti;
	private Categoria categoria;
	
	

	/**
	 * genera un cdg siguiendo al maximo 
	 */
	public void generateCdg(){
		((Httrabajador)getTo()).setCdg(Integer.parseInt(Utils.maxCode("Httrabajador", "cdg"))+1);
	}
	
	

	private List<SelectItem> estciv;

	/**
	 * Recupera los tipos de sexo
	 * 
	 * @return
	 */
	public List<SelectItem> getListaEstciv() {

		Locale locale = FacesContext.getCurrentInstance().getViewRoot()
				.getLocale();
		estciv = new LinkedList<SelectItem>();
		for (EstadoCivil p : EstadoCivil.values()) {
			String name = p.getName(locale);
			SelectItem item = new SelectItem(p, name);
			estciv.add(item);

		}
		return estciv;
	}

	public Categoria getCategoria() {
		return categoria;
	}

	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}

	private List<SelectItem> tipccc;

	public List<SelectItem> getTipccc() {
		return tipccc;
	}

	public void setTipccc(List<SelectItem> tipccc) {
		this.tipccc = tipccc;
	}

	public List<SelectItem> getListatipos() {
		if (tipccc == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot()
					.getLocale();
			tipccc = new LinkedList<SelectItem>();
			for (Tipccc e : Tipccc.values()) {
				String name = e.getName(locale);
				SelectItem item = new SelectItem(e, name);
				tipccc.add(item);
			}
		}
		return tipccc;
	}

	private List<SelectItem> prorrateos;

	public List<SelectItem> getProrrateos() {
		return prorrateos;
	}

	public void setProrrateos(List<SelectItem> prorrateos) {
		this.prorrateos = prorrateos;
	}

	public List<SelectItem> getListaprorateos() {
		if (prorrateos == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot()
					.getLocale();
			prorrateos = new LinkedList<SelectItem>();
			for (Prorateo e : Prorateo.values()) {
				String name = e.getName(locale);
				SelectItem item = new SelectItem(e, name);
				prorrateos.add(item);
			}
		}
		return prorrateos;
	}

	private List<SelectItem> tipirpf;

	public List<SelectItem> getListatipirpf() {
		if (tipirpf == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot()
					.getLocale();
			tipirpf = new LinkedList<SelectItem>();
			for (TipIrpf e : TipIrpf.values()) {
				String name = e.getName(locale);
				SelectItem item = new SelectItem(e, name);
				tipirpf.add(item);
			}
		}
		return tipirpf;
	}

	private List<SelectItem> timeconts;

	public List<SelectItem> getListatimecont() {
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

	public List<SelectItem> getTipirpf() {
		return tipirpf;
	}

	public void setTipirpf(List<SelectItem> tipirpf) {
		this.tipirpf = tipirpf;
	}

	public List<SelectItem> getTimeconts() {
		return timeconts;
	}

	public void setTimeconts(List<SelectItem> timeconts) {
		this.timeconts = timeconts;
	}


	@Override
	public void onEditSearch(ActionEvent arg0) {

		super.onEditSearch(arg0);

		documento = new Documento();
		actividad = new Actividad();
		domicilio = new Domicilio();
		pais =      new Pais();
		provincia = new Provincia();
		provincia1 = new Provincia();
		tipovia =   new Tipovia();
		tipocont =  new ContratosInternos();
		tipcotc2 =  new ContratosTc2();
		entidad =   new Entidad();
		epigrafe =  new Epigrafe();
		sucursal1 = new Sucursal();
		basecoti =  new Base();
		categoria = new Categoria();

	}

	@Override
	public void onSearch(ActionEvent event) {
		try {

			if ((documento.getCdg() != null)
					&& (!StringUtils.isEmpty(documento.getCdg()))) {
				getCriteria().addEqualExpression(
						getFieldName(IPayrollAlias.HTTRABAJADOR_DOCUMENTO_CDG),
						documento.getCdg());
			}

			if ((pais.getCdg() != null)
					&& (!StringUtils.isEmpty(pais.getCdg()))) {
				getCriteria().addEqualExpression(
						getFieldName(IPayrollAlias.HTTRABAJADOR_PAIS_CDG),
						pais.getCdg());
			}

			if ((provincia.getCdg() != null)
					&& (!StringUtils.isEmpty(provincia.getCdg()))) {
				getCriteria().addEqualExpression(
						getFieldName(IPayrollAlias.HTTRABAJADOR_PROVINCIA_CDG),
						provincia.getCdg());
			}
			if ((provincia1.getCdg() != null)
					&& (!StringUtils.isEmpty(provincia1.getCdg()))) {
				getCriteria()
						.addEqualExpression(
								getFieldName(IPayrollAlias.HTTRABAJADOR_PROVINCIA1_CDG),
								provincia1.getCdg());
			}
			if ((tipovia.getCdg() != null)
					&& (!StringUtils.isEmpty(tipovia.getCdg()))) {
				getCriteria().addEqualExpression(
						getFieldName(IPayrollAlias.HTTRABAJADOR_TIPOVIA_CDG),
						tipovia.getCdg());
			}
			if (actividad.getCdg() != null) {
				getCriteria().addEqualExpression(
						getFieldName(IPayrollAlias.HTTRABAJADOR_ACTIVIDAD_CDG),
						actividad.getCdg());
			}
			if (domicilio.getCdg() != null) {
				getCriteria().addEqualExpression(
						getFieldName(IPayrollAlias.HTTRABAJADOR_DOMICILIO_CDG),
						domicilio.getCdg());
			}
			if ((tipcotc2.getCdg() != null)	&& (!StringUtils.isEmpty(tipcotc2.getCdg()))) {
				getCriteria().addEqualExpression(
						getFieldName(IPayrollAlias.HTTRABAJADOR_TIPCOTC2_CDG),
						tipcotc2.getCdg());
			}

			if ((tipocont.getCdg() != null)	&& (!StringUtils.isEmpty(tipocont.getCdg()))) {
				getCriteria().addEqualExpression(
						getFieldName(IPayrollAlias.HTTRABAJADOR_TIPOCONT_CDG),
						tipocont.getCdg());
			}

					if ((entidad.getCdg() != null)
					&& (!StringUtils.isEmpty(entidad.getCdg()))) {
				getCriteria().addEqualExpression(
						getFieldName(IPayrollAlias.HTTRABAJADOR_ENTIDAD_CDG),
						entidad.getCdg());
			}
		/*	if ((sucursal1.getId().getCdg() != null)
					&& (!StringUtils.isEmpty(sucursal1.getId().getCdg()))) {
				getCriteria()
						.addEqualExpression(
								getFieldName(IPayrollAlias.HTTRABAJADOR_SUCURSAL1_ID_CDG),
								sucursal1.getId().getCdg());
			}*/
			if ((basecoti.getCdg() != null)
					&& (!StringUtils.isEmpty(basecoti.getCdg()))) {
				getCriteria().addEqualExpression(
						getFieldName(IPayrollAlias.HTTRABAJADOR_BASECOTI_CDG),
						basecoti.getCdg());
			}
			if ((epigrafe.getCdg() != null)
					&& (!StringUtils.isEmpty(epigrafe.getCdg()))) {
				getCriteria().addEqualExpression(
						getFieldName(IPayrollAlias.HTTRABAJADOR_EPIGRAFE_CDG),
						epigrafe.getCdg());
			}
			/*if ((categoria.getId().getCdg() != null)
					&& (!StringUtils.isEmpty(categoria.getId().getCdg()))) {
				getCriteria().addEqualExpression(
						getFieldName(IPayrollAlias.HTTRABAJADOR_EPIGRAFE_CDG),
						categoria.getId().getCdg());
			}*/

		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		super.onSearch(event);

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

	public ContratosInternos getTipocont() {
		return tipocont;
	}

	public void setTipocont(ContratosInternos tipocont) {
		this.tipocont = tipocont;
	}

	public ContratosTc2 getTipcotc2() {
		return tipcotc2;
	}

	public void setTipcotc2(ContratosTc2 tipcotc2) {
		this.tipcotc2 = tipcotc2;
	}

	public Entidad getEntidad() {
		return entidad;
	}

	public void setEntidad(Entidad entidad) {
		this.entidad = entidad;
	}

	public Epigrafe getEpigrafe() {
		return epigrafe;
	}

	public void setEpigrafe(Epigrafe epigrafe) {
		this.epigrafe = epigrafe;
	}

	public Sucursal getSucursal1() {
		return sucursal1;
	}

	public void setSucursal1(Sucursal sucursal1) {
		this.sucursal1 = sucursal1;
	}

	public Base getBasecoti() {
		return basecoti;
	}

	public void setBasecoti(Base basecoti) {
		this.basecoti = basecoti;
	}

	public Documento getTipdoc() {
		return documento;
	}

	public void setTipdoc(Documento tipdoc) {
		this.documento = tipdoc;
	}

	public Pais getPais() {
		return pais;
	}

	public void setPais(Pais pais) {
		this.pais = pais;
	}

	public Provincia getProvincia() {
		return provincia;
	}

	public void setProvincia(Provincia provincia) {
		this.provincia = provincia;
	}

	public Provincia getProvincia1() {
		return provincia1;
	}

	public void setProvincia1(Provincia provincia1) {
		this.provincia1 = provincia1;
	}

	public Tipovia getTipovia() {
		return tipovia;
	}

	public void setTipovia(Tipovia tipovia) {
		this.tipovia = tipovia;
	}

	public Date getFecnac() {
		return fecnac;
	}

	public void setFecnac(Date fecnac) {
		this.fecnac = fecnac;
	}

	
	  public void verifyNullFields(){
	 if(StringUtils.isEmpty(((Httrabajador)getTo()).getTipovia().getCdg()))
	 ((Httrabajador)getTo()).getTipovia().setCdg("CL");	 
	  if(StringUtils.isEmpty(((Httrabajador)getTo()).getDocumento().getCdg()))
	  ((Httrabajador)getTo()).setDocumento(null);
	  if(StringUtils.isEmpty(((Httrabajador)getTo()).getPais().getCdg()))
	  ((Httrabajador)getTo()).setPais(null);
	 if(StringUtils.isEmpty(((Httrabajador)getTo()).getProvincia().getCdg()))
	  ((Httrabajador)getTo()).setProvincia(null);
	  if(StringUtils.isEmpty(((Httrabajador)getTo()).getProvincia1().getCdg()))
	  ((Httrabajador)getTo()).setProvincia1(null);
	  }
}
