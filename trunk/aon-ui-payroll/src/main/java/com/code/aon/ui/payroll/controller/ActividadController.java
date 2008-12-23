package com.code.aon.ui.payroll.controller;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.hibernate.Query;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.payroll.cotizacion.Linepigr;
import com.code.aon.payroll.dao.IPayrollAlias;
import com.code.aon.payroll.enumeration.EnvioSS2;
import com.code.aon.payroll.enumeration.Epigrafes;
import com.code.aon.payroll.enumeration.IndRegimen;
import com.code.aon.payroll.enumeration.Modpago;
import com.code.aon.payroll.enumeration.Tipcuenta;
import com.code.aon.payroll.enumeration.Tiponomina;
import com.code.aon.payroll.principales.empresa.Emprdom;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.LinesController;

public class ActividadController extends LinesController {



	private List<SelectItem> listaenvioss;
	private List<SelectItem> listamodpago;
	private List<SelectItem> listaindregimen;
	private List<SelectItem> listatiponom;


	

	
	
	
	public List<SelectItem> getListaenvios() {
		if(listaenvioss==null){
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			listaenvioss = new LinkedList<SelectItem>();
			for (EnvioSS2 e : EnvioSS2.values()) {
				String name = e.getName( locale );
				SelectItem item = new SelectItem( e, name );
				listaenvioss.add(item);
			}
		}
		return listaenvioss;
	}

	public List<SelectItem> getListamodpagos() {
		if(listamodpago==null){
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			listamodpago = new LinkedList<SelectItem>();
			for (Modpago e : Modpago.values()) {
				String name = e.getName( locale );
				SelectItem item = new SelectItem( e, name );
				listamodpago.add(item);
			}
		}
		return listamodpago;
	}

	public List<SelectItem> getListaindregs() {
		if(listaindregimen==null){
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			listaindregimen = new LinkedList<SelectItem>();
			for (IndRegimen e : IndRegimen.values()) {
				String name = e.getName( locale );
				SelectItem item = new SelectItem( e, name );
				listaindregimen.add(item);
			}
		}
		return listaindregimen;
	}

	
	public List<SelectItem> getListatiposnom() {
		if(listatiponom==null){
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			listatiponom = new LinkedList<SelectItem>();
			for (Tiponomina e : Tiponomina.values()) {
				String name = e.getName( locale );
				SelectItem item = new SelectItem( e, name );
				listatiponom.add(item);
			}
		}
		return listatiponom;
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
	




	

}
