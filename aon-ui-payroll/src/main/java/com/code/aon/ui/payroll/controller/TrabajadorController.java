package com.code.aon.ui.payroll.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.hibernate.Query;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.payroll.dao.IPayrollAlias;
import com.code.aon.payroll.enumeration.IndiceAgrario;
import com.code.aon.payroll.enumeration.IndiceGrupo;
import com.code.aon.payroll.principales.Domicilio;
import com.code.aon.payroll.principales.empresa.Actividad;
import com.code.aon.payroll.principales.empresa.Emprccos;
import com.code.aon.payroll.principales.empresa.Empresa;
import com.code.aon.payroll.principales.persona.Trabajador;
import com.code.aon.ui.form.LinesController;

public class TrabajadorController extends LinesController {
	
	private static final Logger LOGGER = Logger.getLogger(CotizacionBonificacionController.class.getName());

	private List<SelectItem> indagrario;
	private List<SelectItem> indgrupo;
	
	private Empresa empresa;
	private Actividad actividad;
	private Domicilio domicilio;
	private Emprccos emprccos;
		
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
	
	private Domicilio lazyDomicilio;

	public Domicilio getLazyDomicilio() {
		
		return lazyDomicilio;
	}

	public void setLazyDomicilio(Domicilio lazyDomicilio) {
		this.lazyDomicilio = lazyDomicilio;
	}
	
	
	
	/**
	 * Recupera el domicilio directamente de la BD a traves de hibernate.
	 * Metodo para salvar el problema del EAGER/LAZY de esta BD ctSql
	 * 
	 */
	public void obtainDomicilio(){
		
		Integer cdg = ((Trabajador)getTo()).getCdg();
		
		String sql = "select * from Domicilio where cdg="+cdg.toString();
		System.out.println(sql);
		String factoryName = HibernateUtil.getSessionFactoryName(null);
		Query query = HibernateUtil.getSession(factoryName).createQuery(sql);			
		List results = query.list();
		
		// return results.get(0).toString();
	}
	
	@Override
	public void onReset(ActionEvent event) {
		// TODO Auto-generated method stub
		super.onReset(event);
		this.setDomicilio(new Domicilio());
		Integer cdg = Integer.parseInt(Utils.maxCode("Trabajador", "cdg"));
		((Trabajador)this.getTo()).setCdg(cdg+1);
	}

	
	
	
}
