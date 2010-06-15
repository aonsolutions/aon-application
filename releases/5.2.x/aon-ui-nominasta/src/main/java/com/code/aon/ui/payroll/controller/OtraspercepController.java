package com.code.aon.ui.payroll.controller;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
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
import com.code.aon.ui.form.PageDataModel;

public class OtraspercepController extends PayrollBasicController {

	private static final Logger LOGGER = Logger.getLogger(OtraspercepController.class.getName());
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
	public void onEditSearch(ActionEvent event) {
		super.onEditSearch(event);
		setPersona(new Persona());
		setEmpresa(new Empresa());

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
	
	public void generateCdg() {
		((Otrperc) getTo()).setCdg(Integer.parseInt(Utils.maxCode(
				"Otrperc", "cdg")) + 1);
	}

	private Integer code ;
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
		c.setImporte(new BigDecimal(0.00));
		c.setPrcret(new BigDecimal(0.00));
		c.setBase(new BigDecimal(0.00));
	}
	
	
	
	
	
	/*
	 * **************************************************
	 * PRUEBAS SQL sin hibernate
	 * **************************************************
	 */
	
	@Override
	public void onSelect(ActionEvent event) {
		// TODO Auto-generated method stub
		super.onSelect(event);
		onPrintSelected();
		// despues del on Select obtener por SQL persona y empresa 
		// y guardarlos en el controller
		try {
			getPersonaByCdg();
			getEmpresaByCdg();
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	/**
	 * @throws ManagerBeanException 
	 * 
	 */
	private void getPersonaByCdg() throws ManagerBeanException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		Integer cdg = ((Otrperc)this.getTo()).getCodper();
		getPersona().setCdg(cdg);
		try {
			StringWriter stmt = new StringWriter();
			stmt.append("SELECT nombre, descripcion, apellido2");
			stmt.append(" FROM persona per");
			stmt.append(" WHERE per.cdg = " + cdg);
			ps = HibernateUtil.getSQLConnection().prepareStatement(
					stmt.toString(), ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			rs = ps.executeQuery();
			
			if(rs.first()){
				getPersona().setNombre(rs.getString(1));
				getPersona().setDescripcion(rs.getString(2));
				getPersona().setApellido2(rs.getString(3));
			} else {
				// NO HUBO RESULTADOS DE LA SELECT
			}
		} catch (SQLException e) {
			throw new ManagerBeanException(e.getMessage(), e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
				}
			}
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
				}
			}
		}
	}

	/**
	 * @throws ManagerBeanException 
	 * 
	 */
	private void getEmpresaByCdg() throws ManagerBeanException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		Integer cdg = ((Otrperc)this.getTo()).getCodemp();
		getEmpresa().setCdg(cdg);
		try {
			StringWriter stmt = new StringWriter();
			stmt.append("SELECT descripcion");
			stmt.append(" FROM emprnif emp");
			stmt.append(" WHERE emp.cdg = " + cdg);
			ps = HibernateUtil.getSQLConnection().prepareStatement(
					stmt.toString(), ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			rs = ps.executeQuery();
			
			if(rs.first()){
				//getEmpresa().setNombre(rs.getString(1));
				getEmpresa().setDescripcion(rs.getString(1));
			} else {
				// NO HUBO RESULTADOS DE LA SELECT
			}
		} catch (SQLException e) {
			throw new ManagerBeanException(e.getMessage(), e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
				}
			}
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
				}
			}
		}
	}
	
	/**
	 * Update the value of codper and codemp of the bean
	 * 
	 */
	public void updateBeanJoins(){
		((Otrperc)this.getTo()).setCodemp(empresa.getCdg());
		((Otrperc)this.getTo()).setCodper(persona.getCdg());
	}
	
	public void onPrintSelected() {
		PayrollJasperTemplateController.addSelectedToList(getTo());
	}
	
}
