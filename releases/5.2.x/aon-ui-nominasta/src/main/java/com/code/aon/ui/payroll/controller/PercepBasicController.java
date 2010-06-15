package com.code.aon.ui.payroll.controller;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.payroll.auxiliares.convenios.Complemento;
import com.code.aon.payroll.dao.IPayrollAlias;
import com.code.aon.payroll.enumeration.FijoVariable;
import com.code.aon.payroll.enumeration.IndiceComplemento;
import com.code.aon.payroll.enumeration.Retribuciones;
import com.code.aon.payroll.enumeration.TipoComplemento;
import com.code.aon.payroll.principales.persona.Trabajador;

public class PercepBasicController extends PayrollBasicController {

	private List<SelectItem> complementos;
	private List<SelectItem> retribuciones;
	private List<SelectItem> fijoVariable;
	private List<SelectItem> indComp;

	private Date fecini;
	private Date fecfin;
	private Date fecret;
	private Complemento complemento;
	private Complemento complemento1;
	private Trabajador trabajador;
	private List<ITransferObject> selectedList;

	public List<SelectItem> getListaComplementos() {
		refreshComplementos();
		return complementos;
	}

	public void refreshComplementos() {
		Locale locale = FacesContext.getCurrentInstance().getViewRoot()
				.getLocale();
		complementos = new LinkedList<SelectItem>();
		for (TipoComplemento complemento : TipoComplemento.values()) {
			String name = complemento.getName(locale);
			SelectItem item = new SelectItem(complemento, name);
			complementos.add(item);
		}
	}

	/**
	 * Recupera los tipos de retribuciones
	 * 
	 * @return
	 */
	public List<SelectItem> getListaRetribuciones() {
		if (retribuciones == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot()
					.getLocale();
			retribuciones = new LinkedList<SelectItem>();
			for (Retribuciones retribucion : Retribuciones.values()) {
				String name = retribucion.getName(locale);
				SelectItem item = new SelectItem(retribucion, name);
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
		if (fijoVariable == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot()
					.getLocale();
			fijoVariable = new LinkedList<SelectItem>();
			for (FijoVariable ret : FijoVariable.values()) {
				String name = ret.getName(locale);
				SelectItem item = new SelectItem(ret, name);
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
		if (indComp == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot()
					.getLocale();
			indComp = new LinkedList<SelectItem>();
			for (IndiceComplemento ic : IndiceComplemento.values()) {
				String name = ic.getName(locale);
				SelectItem item = new SelectItem(ic, name);
				indComp.add(item);
			}
		}
		return indComp;
	}

	@Override
	public void onEditSearch(ActionEvent arg0) {
		super.onEditSearch(arg0);

		setComplemento(new Complemento());
		setComplemento1(new Complemento());
		setTrabajador(new Trabajador());

	}

	@Override
	public void onSearch(ActionEvent event) {

		try {
			if (complemento1.getCdg() != null && (!StringUtils.isEmpty(complemento1.getCdg()))) {
				getCriteria().addEqualExpression(getFieldName(IPayrollAlias.PERCEP_COMPLEMENTO1_CDG), getComplemento1().getCdg());
			} 
			if (complemento.getCdg() != null && (!StringUtils.isEmpty(complemento.getCdg()))) {
				getCriteria().addEqualExpression(getFieldName(IPayrollAlias.PERCEP_COMPLEMENTO_CDG),	getComplemento().getCdg());
			} if (trabajador.getCdg() != null) {
				getCriteria().addEqualExpression(getFieldName(IPayrollAlias.PERCEP_TRABAJADOR_CDG), getTrabajador().getCdg());
			}
			if (fecini != null) {
				getCriteria().addEqualExpression(getFieldName(IPayrollAlias.PERCEP_FECINI), getFecini());
			}
			if (fecfin != null) {
				getCriteria().addEqualExpression(getFieldName(IPayrollAlias.PERCEP_FECFIN), getFecfin());
			}
			if (fecret != null) {
				getCriteria().addEqualExpression(getFieldName(IPayrollAlias.PERCEP_FECRET), getFecret());
			}
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		super.onSearch(event);
	}
	
	
	@Override
	public void onSelect(ActionEvent event) {
		selectedList = new LinkedList<ITransferObject>();
		super.onSelect(event);		
		selectedList.add(this.getTo());		
						
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

	public Date getFecret() {
		return fecret;
	}

	public void setFecret(Date fecret) {
		this.fecret = fecret;
	}

	public Complemento getComplemento() {
		return complemento;
	}

	public void setComplemento(Complemento complemento) {
		this.complemento = complemento;
	}

	public Complemento getComplemento1() {
		return complemento1;
	}

	public void setComplemento1(Complemento complemento1) {
		this.complemento1 = complemento1;
	}

	public Trabajador getTrabajador() {
		return trabajador;
	}

	public void setTrabajador(Trabajador trabajador) {
		this.trabajador = trabajador;
	}
	

	public List<ITransferObject> getSelectedList() {
		return selectedList;
	}

	public void setSelectedList(List<ITransferObject> selectedList) {
		this.selectedList = selectedList;
	}

	
}
