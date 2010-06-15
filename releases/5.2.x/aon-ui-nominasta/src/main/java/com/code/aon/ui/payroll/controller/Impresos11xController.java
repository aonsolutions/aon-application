package com.code.aon.ui.payroll.controller;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.payroll.auxiliares.Admon;
import com.code.aon.payroll.dao.IPayrollAlias;
import com.code.aon.payroll.enumeration.ModalidadImpuesto;
import com.code.aon.payroll.enumeration.TipoImpreso;
import com.code.aon.payroll.geograficas.Provincia;
import com.code.aon.payroll.principales.empresa.Empresa;
import com.code.aon.payroll.resultados.irpf.Impresos11x;

public class Impresos11xController extends PayrollBasicController {

	// Falta implementacion imprimir formulario "Informativa 11X"
	// Falta implementacion enviar email
	
	private static final Logger LOGGER = Logger.getLogger(CotizacionBonificacionController.class.getName());

	private List<SelectItem> listaTipo;

	/**
	 * Recupera los tipos de impreso
	 * 
	 * @return
	 */
	public List<SelectItem> getListaTipoImpreso() {
		if (listaTipo == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot()
					.getLocale();
			listaTipo = new LinkedList<SelectItem>();
			for (TipoImpreso p : TipoImpreso.values()) {
				String name = p.getName(locale);
				SelectItem item = new SelectItem(p, name);
				listaTipo.add(item);
			}
		}
		return listaTipo;
	}

	private List<SelectItem> listaModimpuesto;

	/**
	 * Recupera las modalidades de impreso
	 * 
	 * @return
	 */
	public List<SelectItem> getListaModimpuesto() {
		if (listaModimpuesto == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot()
					.getLocale();
			listaModimpuesto = new LinkedList<SelectItem>();
			for (ModalidadImpuesto p : ModalidadImpuesto.values()) {
				String name = p.getName(locale);
				SelectItem item = new SelectItem(p, name);
				listaModimpuesto.add(item);
			}
		}
		return listaModimpuesto;
	}

	/**
	 * Genera un numero autonumerico para el codigo de la paga extra
	 * @param event
	 */
	public void generarNumero(ActionEvent event) {

		String num = Utils.maxCode("Impresos11x", "cdg");
		((Impresos11x) getTo()).setCdg(Integer.parseInt(num) + 1);
	}

	
	/**
	 * Establece valores por defecto a los campos nulos
	 */
	public void setDefaultFields() {
		
		Impresos11x to =(Impresos11x) getTo();
		Calendar calendar = Calendar.getInstance();
		
		if(to.getTradinper()==null)
			to.setTradinper(0);
		if(to.getTradinimp()==null)
			to.setTradinimp(new BigDecimal(0));
		if(to.getTradinret()==null)
			to.setTradinret(new BigDecimal(0));
		if(to.getTraespper()==null)
			to.setTraespper(0);
		if(to.getTraespimp()==null)
			to.setTraespimp(new BigDecimal(0));
		if(to.getTraespret()==null)
			to.setTraespret(new BigDecimal(0));
		if(to.getActdinper()==null)
			to.setActdinper(0);
		if(to.getActdinimp()==null)
			to.setActdinimp(new BigDecimal(0));
		if(to.getActdinret()==null)
			to.setActdinret(new BigDecimal(0));
		if(to.getActespper()==null)
			to.setActespper(0);
		if(to.getActespimp()==null)
			to.setActespimp(new BigDecimal(0));
		if(to.getActespret()==null)
			to.setActespret(new BigDecimal(0));
		if(to.getPredinper()==null)
			to.setPredinper(0);
		if(to.getPredinimp()==null)
			to.setPredinimp(new BigDecimal(0));
		if(to.getPredinret()==null)
			to.setPredinret(new BigDecimal(0));
		if(to.getPreespper()==null)
			to.setPreespper(0);
		if(to.getPreespimp()==null)
			to.setPreespimp(new BigDecimal(0));
		if(to.getPreespret()==null)
			to.setPreespret(new BigDecimal(0));
		if(to.getLiqtotal()==null)
			to.setLiqtotal(new BigDecimal(0));
		if(to.getImgper()==null)
			to.setImgper(0);
		if(to.getImgimp()==null)
			to.setImgimp(new BigDecimal(0));
		if(to.getImgret()==null)
			to.setImgret(new BigDecimal(0));
		if(to.getFecha()==null)
			to.setFecha(calendar.getTime());
		if(StringUtils.isEmpty(to.getAdmon().getCdg()))
			to.setAdmon(null);
		if(StringUtils.isEmpty(to.getProvincia().getCdg()))
			to.setProvincia(null);
		if(to.getMes()==null)
			to.setMes(calendar.get(Calendar.MONTH)+1);
		if(to.getTrimestre()==null)
			to.setTrimestre((calendar.get((Calendar.MONTH))/3)+1);
		if(to.getAnio()==null)
			to.setAnio(calendar.get(Calendar.YEAR));
		
	}
	
	private Empresa emprnif;
	private Admon admon;
	private Provincia provincia;
	private Date fecha;
	private Date fecremimp;
	private TipoImpreso tipo;
	private ModalidadImpuesto modimpuesto;
	
	@Override
	public void onEditSearch(ActionEvent arg0) {
		
		super.onEditSearch(arg0);
		
		emprnif = new Empresa();
		admon = new Admon();
		provincia = new Provincia();
		tipo=null;
		modimpuesto=null;
		fecha=null;
		fecremimp=null;
	}

	/**
	 * Se incluyen manualmente a las búsquedas los campos lookup y de fechas 
	 */
	@Override
	public void onSearch(ActionEvent event) {

		try {
			//Búsqueda por campos LookUp
			if (emprnif!=null && (emprnif.getCdg() != null)) {
				getCriteria().addEqualExpression(getFieldName(IPayrollAlias.IMPRESOS11X_EMPRNIF_CDG), emprnif.getCdg());
			}
			if (admon!=null && StringUtils.isNotEmpty(admon.getCdg())) {
				getCriteria().addEqualExpression(getFieldName(IPayrollAlias.IMPRESOS11X_ADMON_CDG), admon.getCdg());
			}
			if (provincia!=null && StringUtils.isNotEmpty(provincia.getCdg())) {
				getCriteria().addEqualExpression(getFieldName(IPayrollAlias.IMPRESOS11X_PROVINCIA_CDG), provincia.getCdg());
			}
			//Búsqueda por campos Date
			if (fecha != null) {
				getCriteria().addEqualExpression(getFieldName(IPayrollAlias.IMPRESOS11X_FECHA), fecha);
			}
			if (fecremimp != null) {
				getCriteria().addEqualExpression(getFieldName(IPayrollAlias.IMPRESOS11X_FECREMIMP), fecremimp);
			}
			//Búsqueda por campos selectOneMenu
			if (tipo!= null) {
				getCriteria().addEqualExpression(getFieldName(IPayrollAlias.IMPRESOS11X_TIPO), tipo);
			}
			if (modimpuesto!= null) {
				getCriteria().addEqualExpression(getFieldName(IPayrollAlias.IMPRESOS11X_MODIMPUESTO), modimpuesto);
			}
			
		} catch (ManagerBeanException e) {
			LOGGER.log( Level.SEVERE, e.getMessage(), e );
			e.printStackTrace();
		}

		super.onSearch(event);
	}

	public Empresa getEmprnif() {
		return emprnif;
	}

	public void setEmprnif(Empresa emprnif) {
		this.emprnif = emprnif;
	}

	public Admon getAdmon() {
		return admon;
	}

	public void setAdmon(Admon admon) {
		this.admon = admon;
	}

	public Provincia getProvincia() {
		return provincia;
	}

	public void setProvincia(Provincia provincia) {
		this.provincia = provincia;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public Date getFecremimp() {
		return fecremimp;
	}

	public void setFecremimp(Date fecremimp) {
		this.fecremimp = fecremimp;
	}
	
	public TipoImpreso getTipo() {
		return tipo;
	}

	public void setTipo(TipoImpreso tipo) {
		this.tipo = tipo;
		((Impresos11x)getTo()).setTipo(tipo);
	}
	
	public ModalidadImpuesto getModimpuesto() {
		return modimpuesto;
	}

	public void setModimpuesto(ModalidadImpuesto modimpuesto) {
		this.modimpuesto = modimpuesto;
		((Impresos11x)getTo()).setModimpuesto(modimpuesto);
	}
	
	
	
	/**
	 * Devuelve si el tipo es 110
	 * @return
	 */
	public boolean isImpreso110(){
		return((Impresos11x)getTo()).getTipo() == TipoImpreso.IMPRESO110;
	}
	
	/**
	 * Devuelve si el tipo es 111
	 * @return
	 */
	public boolean isImpreso111(){
		return((Impresos11x)getTo()).getTipo() == TipoImpreso.IMPRESO111;
	}
	
	/**
	 * Devuelve si la modalidad es de ventanilla o no
	 * 
	 * @return
	 */
	public boolean isVentanilla() {
		System.out.println("isVentanilla");
		return ((Impresos11x) getTo()).getModimpuesto() == ModalidadImpuesto.VENTANILLA;
	}

	/**
	 * Devuelve si la modalidad es de domiciliacion o no
	 * 
	 * @return
	 */
	public boolean isDomiciliacion() {
		System.out.println("isDomiciliacion");
		return ((Impresos11x) getTo()).getModimpuesto() == ModalidadImpuesto.DOMICILIACION;
	}
	
	/**
	 * Devuelve si la modalidad es de domiciliacion o no
	 * 
	 * @return
	 */
	public boolean isTrasmiteCliente() {
		System.out.println("isTrasmiteCliente");
		return ((Impresos11x) getTo()).getModimpuesto() == ModalidadImpuesto.CLIENTE;
	}

	/**
	 * Establece el valor por defecto de la administracion de hacienda 
	 */
	public void setDefaultAdmon(ActionEvent event) {
		((Impresos11x) getTo()).setAdmon(((Impresos11x) getTo()).getEmprnif().getAdmon());
	}
	
	/**
	 * Establece el valor por defecto de la provincia 
	 */
	public void setDefaultProvincia(ActionEvent event) {
		System.out.println("setDefaultProvincia");
		
		String cdg = ((Impresos11x) getTo()).getEmprnif().getAdmon().getCdg().substring(0, 2);
		((Impresos11x) getTo()).getProvincia().setCdg(cdg);
	}
	
	
	
	/**
	 * Comprueba que los valores de mes y trimestre este dentro de los rangos 
	 * @throws Exception 
	 */
	public void verifyMonthFields(ActionEvent event) throws Exception {
		System.out.println("verifyMonthFields");
		
		if(((Impresos11x) getTo()).getMes()>12 || ((Impresos11x) getTo()).getMes()<1)
			throw new Exception("Mes fuera de rango"); 
		if(((Impresos11x) getTo()).getTrimestre()>4 || ((Impresos11x) getTo()).getTrimestre()<1)
			throw new Exception("Trimestre fuera de rango");
	}
	
	
}
