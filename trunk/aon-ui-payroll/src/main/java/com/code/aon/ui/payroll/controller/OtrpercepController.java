package com.code.aon.ui.payroll.controller;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.hibernate.Query;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.payroll.dao.IPayrollAlias;
import com.code.aon.payroll.enumeration.Claveper;
import com.code.aon.payroll.enumeration.Ingreso;
import com.code.aon.payroll.enumeration.Retribuciones;
import com.code.aon.payroll.principales.empresa.Empresa;
import com.code.aon.payroll.principales.personas.Otrperc;
import com.code.aon.payroll.principales.personas.Persona;
import com.code.aon.ui.form.LinesController;

public class OtrpercepController extends LinesController {

	private Date fecha;
	private Persona persona;
	private Empresa empresa;
	private List<SelectItem> clavesper;
	private List<SelectItem> retribuciones;
	private List<SelectItem> ingresos;
	
	
	public List<SelectItem> getListaClaves() {
		if(clavesper==null){
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			clavesper = new LinkedList<SelectItem>();
			for (Claveper ia : Claveper.values()) {
				String name = ia.getName( locale );
				SelectItem item = new SelectItem( ia, name );
				clavesper.add(item);
			}
		}
		return clavesper;
	}
	

	public List<SelectItem> getListaRetribuciones() {
		if(retribuciones==null){
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			retribuciones = new LinkedList<SelectItem>();
			for (Retribuciones tp : Retribuciones.values()) {
				String name = tp.getName( locale );
				SelectItem item = new SelectItem( tp, name );
				retribuciones.add(item);
			}
		}
		return retribuciones;
	}
	
	
	
	public List<SelectItem> getListaIngreso() {
		if(ingresos==null){
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			ingresos = new LinkedList<SelectItem>();
			for (Ingreso tp : Ingreso.values()) {
				String name = tp.getName( locale );
				SelectItem item = new SelectItem( tp, name );
				ingresos.add(item);
			}
		}
		return ingresos;
	}

	@Override
	public void onEditSearch(ActionEvent arg0) {
	       setPersona( new Persona() );
		   setEmpresa( new Empresa() );
		   super.onEditSearch(arg0);
	}

	@Override
	public void onSearch(ActionEvent event) {
	
		try {
			if (empresa.getCdg() != null)  {
				getCriteria().addEqualExpression(getFieldName(IPayrollAlias.OTRPERC_CODEMP), getEmpresa().getCdg());
			}
			if (persona.getCdg() != null) {
				getCriteria().addEqualExpression(getFieldName(IPayrollAlias.OTRPERC_CODPER), getPersona().getCdg());
			}
		     
		    if (fecha != null){
				getCriteria().addEqualExpression(getFieldName(IPayrollAlias.OTRPERC_FECHA), getFecha());
			}
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		super.onSearch(event);
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public Persona getPersona() {
		return persona;
	}

	public void setPersona(Persona persona) {
		this.persona = persona;
	}

	public Empresa getEmpresa() {
		return empresa;
	}

	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}
	
	Integer code ;
    public Integer getCode() throws ManagerBeanException {	
       	code = 0;		
		String consulta = "select max(cdg) from Otrperc";			
		Query q = HibernateUtil.getSession().createQuery(consulta);			
		List results = q.list();
		System.out.println("Max Code: " + results.get(0));   
		code= (Integer)results.get(0) +1;
		System.out.println("New Code: " + code);
		return code;		      
	}

    public void setCode(Integer code) {
		this.code = code;
	}
	
	public void setDefaultFields(){		
		Otrperc c = (Otrperc)getTo();		
		c.setClave(Claveper.CLAVE7);
		c.setNatret(Retribuciones.DINERARIA);
		c.setIngreso(Ingreso.ING1);
	}
	
	
		
}
