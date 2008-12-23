package com.code.aon.ui.payroll.controller;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;

import com.code.aon.payroll.enumeration.ModalidadImpuesto;
import com.code.aon.payroll.enumeration.TipoImpreso;
import com.code.aon.payroll.resultados.irpf.Impr11x;

public class Impresos11xController extends PayrollBasicController {

	// Falta implementacion imprimir formulario "Informativa 11X"
	// Falta implementacion enviar email

	private List<SelectItem> tipo;

	/**
	 * Recupera los tipos de impreso
	 * 
	 * @return
	 */
	public List<SelectItem> getListaTipoImpreso() {
		if (tipo == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot()
					.getLocale();
			tipo = new LinkedList<SelectItem>();
			for (TipoImpreso p : TipoImpreso.values()) {
				String name = p.getName(locale);
				SelectItem item = new SelectItem(p, name);
				tipo.add(item);
			}
		}
		return tipo;
	}

	private List<SelectItem> modimpuesto;

	/**
	 * Recupera las modalidades de impreso
	 * 
	 * @return
	 */
	public List<SelectItem> getListaModimpuesto() {
		if (modimpuesto == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot()
					.getLocale();
			modimpuesto = new LinkedList<SelectItem>();
			for (ModalidadImpuesto p : ModalidadImpuesto.values()) {
				String name = p.getName(locale);
				SelectItem item = new SelectItem(p, name);
				modimpuesto.add(item);
			}
		}
		return modimpuesto;
	}

	/**
	 * Devuelve si la modalidad es de ventanilla o no
	 * 
	 * @return
	 */
	public boolean isVentanilla() {
		System.out.println("isVentanilla");
		return ((Impr11x) getTo()).getModimpuesto() == ModalidadImpuesto.VENTANILLA;
	}

	/**
	 * Devuelve si la modalidad es de domiciliacion o no
	 * 
	 * @return
	 */
	public boolean isDomiciliacion() {
		System.out.println("isDomiciliacion");
		return ((Impr11x) getTo()).getModimpuesto() == ModalidadImpuesto.DOMICILIACION;
	}

	public void generarNumero(ActionEvent event) {

		String num = Utils.maxCode("Impr11x", "cdg");
		((Impr11x) getTo()).setCdg(Integer.parseInt(num) + 1);
	}

	
	/**
	 * Establece valores por defecto a los campos nulos
	 */
	public void setDefaultFields() {
		
		Impr11x to =(Impr11x) getTo();
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
		
		
	}

}
