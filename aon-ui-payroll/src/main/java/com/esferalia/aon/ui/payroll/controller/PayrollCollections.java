package com.esferalia.aon.ui.payroll.controller;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.model.SelectItem;

import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.PayrollException;
import com.esferalia.aon.payroll.core.commons.CommonsPayrollDAOFactory;
import com.esferalia.aon.payroll.core.commons.ICommonsPayrollDAO;
import com.esferalia.aon.payroll.core.cotizacion.ITipoBonificacion;
import com.esferalia.aon.payroll.core.enumeration.CausaAlta;
import com.esferalia.aon.payroll.core.enumeration.Periodicidad;
import com.esferalia.aon.payroll.core.enumeration.TipoContingencia;

public class PayrollCollections implements Serializable {

	private static final long serialVersionUID = -3260568450232494562L;

	private List<SelectItem> tiposContigencia;
	private List<SelectItem> periodicidades;
	private List<SelectItem> tiposBonificacion;
	private List<SelectItem> causasAlta;

	private ICommonsPayrollDAO commonsPayrollDAO;

	public List<SelectItem> getTiposContingencia() {
		if (tiposContigencia == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot()
					.getLocale();
			tiposContigencia = new LinkedList<SelectItem>();
			TipoContingencia[] tipos = TipoContingencia.values();
			for (TipoContingencia tp : tipos) {
				String name = tp.getName(locale);
				SelectItem item = new SelectItem(tp, name);
				tiposContigencia.add(item);
			}
		}
		return tiposContigencia;
	}

	public List<SelectItem> getPeriodicidades() {
		if (periodicidades == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot()
					.getLocale();
			periodicidades = new LinkedList<SelectItem>();
			Periodicidad[] tipos = Periodicidad.values();
			for (Periodicidad tp : tipos) {
				String name = tp.getName(locale);
				SelectItem item = new SelectItem(tp, name);
				periodicidades.add(item);
			}
		}
		return periodicidades;
	}

	private ICommonsPayrollDAO getCommonsPayrollDAO() {
		if (commonsPayrollDAO == null) {
			commonsPayrollDAO = CommonsPayrollDAOFactory.getInstance()
					.getCommonsPayrollDAO();
		}
		return commonsPayrollDAO;
	}

	public List<SelectItem> getTiposBonificacion() {
		try {
			if (tiposBonificacion == null) {
				tiposBonificacion = new LinkedList<SelectItem>();
				List<ITipoBonificacion> list = getCommonsPayrollDAO()
						.getTiposBonificacion(null);
				for (ITipoBonificacion tb : list) {
					String name = tb.getDescripcion();
					SelectItem item = new SelectItem(tb, name, name);
					tiposBonificacion.add(item);
				}
			}
			return tiposBonificacion;
		} catch (PayrollException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
		}
	}
	
	public List<SelectItem> getCausasAlta() {
		if (causasAlta == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot()
					.getLocale();
			causasAlta = new LinkedList<SelectItem>();
			CausaAlta[] causas = CausaAlta.values();
			for (CausaAlta ca : causas) {
				String name = ca.getName(locale);
				SelectItem item = new SelectItem(ca, name);
				causasAlta.add(item);
			}
		}
		return causasAlta;
	}

}
