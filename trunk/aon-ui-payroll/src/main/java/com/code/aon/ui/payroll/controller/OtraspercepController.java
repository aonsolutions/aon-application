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
				getCriteria().addEqualExpression(getFieldName(IPayrollAlias.OTRPERC_EMPRESA_CDG), getEmpresa().getCdg());
			}
			if (persona.getCdg() != null) {
				getCriteria().addEqualExpression(getFieldName(IPayrollAlias.OTRPERC_PERSONA_CDG), getPersona().getCdg());
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
	
	
	
	
	

	@Override
	public void initializeModel() {
		DataModel myModel;
		int count;
		try {
			myModel = new ListDataModel(initializePercepModel());
			
			//count = getManagerBean().getCount(getCriteria());
			//myModel = new PageDataModel(this, count, getPageLimit());
			
			
			
		} catch (ManagerBeanException e) {
			LOGGER.severe(">>>> initializeModel " + e.getMessage());
			addMessage(e.getMessage());
			try {
				clearCriteria();
			} catch (ManagerBeanException e1) {
				LOGGER.severe(">>>> Unable to clear crtieria! " + e.getMessage());
				addMessage(e.getMessage());
			}
			throw new AbortProcessingException(e.getMessage(), e);
		}
		
		
		this.setModel(myModel);
		
		super.initializeModel();
	}	
	
	
	private List<Otrperc> initializePercepModel() throws ManagerBeanException{
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			
			/*
			select cdg, codemp, codper , fecha as fecha97_25_, this_.base as base97_25_, this_.importe as importe97_25_, this_.anio as anio97_25_, this_.concepto as concepto97_25_, this_.clave as clave97_25_, this_.prcret as prcret97_25_, this_.retencion as retenc9_97_25_, this_.aporta_ss as aporta10_97_25_, this_.ingreso as ingreso97_25_, this_.subclave as subclave97_25_, this_.natret as natret97_25_, empresa2_.cdg as cdg75_0_, empresa2_.alias as alias75_0_, empresa2_.descripcion as descri3_75_0_, empresa2_.codcli as codcli75_0_, empresa2_.fecini as fecini75_0_, empresa2_.fecfin as fecfin75_0_, empresa2_.indcal as indcal75_0_, empresa2_.indnom as indnom75_0_, empresa2_.indcoste as indcoste75_0_, empresa2_.fecnew as fecnew75_0_, empresa2_.hornew as hornew75_0_, empresa2_.fecmod as fecmod75_0_, empresa2_.hormod as hormod75_0_, empresa2_.envioss as envioss75_0_, empresa2_.numdoc as numdoc75_0_, empresa2_.representante as repre15_75_0_, empresa2_.cargo as cargo75_0_, empresa2_.fecnac as fecnac75_0_, empresa2_.nrodocrep as nrodo18_75_0_, empresa2_.sexo as sexo75_0_, empresa2_.feccon as feccon75_0_, empresa2_.obsnif as obsnif75_0_, empresa2_.datreg as datreg75_0_, empresa2_.indirpf as indirpf75_0_, empresa2_.cecon as cecon75_0_, empresa2_.modimpuesto as modim25_75_0_, empresa2_.codadm as codadm75_0_, empresa2_.divisa as divisa75_0_, empresa2_.inddoc as inddoc75_0_, empresa2_.tipdocrep as tipdo31_75_0_, empresa2_.tipempr as tipempr75_0_, empresa2_.paiemi as paiemi75_0_, empresa2_.paidocrep as paido33_75_0_, cliente3_.cdg as cdg72_1_, cliente3_.alias as alias72_1_, cliente3_.descripcion as descri3_72_1_, cliente3_.fecini as fecini72_1_, cliente3_.fecfin as fecfin72_1_, cliente3_.codpos as codpos72_1_, cliente3_.indcal as indcal72_1_, cliente3_.indnom as indnom72_1_, cliente3_.indcoste as indcoste72_1_, cliente3_.fecnew as fecnew72_1_, cliente3_.hornew as hornew72_1_, cliente3_.fecmod as fecmod72_1_, cliente3_.hormod as hormod72_1_, cliente3_.envioss as envioss72_1_, cliente3_.numdoc as numdoc72_1_, cliente3_.divisa as divisa72_1_, cliente3_.inddoc as inddoc72_1_, cliente3_.tipemp as tipemp72_1_, cliente3_.paiemi as paiemi72_1_, cliente3_.persona as persona72_1_, cliente3_.tipovia as tipovia72_1_, cliente3_.nomvia as nomvia72_1_, cliente3_.numero as numero72_1_, cliente3_.otrdir as otrdir72_1_, cliente3_.localidad as local20_72_1_, cliente3_.telefono as telefono72_1_, cliente3_.fax as fax72_1_, cliente3_.email as email72_1_, cliente3_.provincia as provi31_72_1_, cliente3_.telefono2 as telefono24_72_1_, cliente3_.telefono3 as telefono25_72_1_, cliente3_.obscli as obscli72_1_, cliente3_.inactivo as inactivo72_1_, cliente3_.soloases as soloases72_1_, cliente3_.coddlg as coddlg72_1_, divisa4_.cdg as cdg37_2_, divisa4_.descripcion as descri2_37_2_, divisa4_.redondeo as redondeo37_2_, divisa4_.mask1 as mask4_37_2_, divisa4_.mask2 as mask5_37_2_, documento5_.cdg as cdg14_3_, documento5_.descripcion as descri2_14_3_, empresario6_.cdg as cdg15_4_, empresario6_.descripcion as descri2_15_4_, pais7_.cdg as cdg54_5_, pais7_.descripcion as descri2_54_5_, tipovia8_.cdg as cdg21_6_, tipovia8_.descripcion as descri2_21_6_, provincia9_.cdg as cdg55_7_, provincia9_.descripcion as descri2_55_7_, provincia9_.compro as compro55_7_, delegacion10_.cdg as cdg39_8_, delegacion10_.descripcion as descri2_39_8_, delegacion10_.codpos as codpos39_8_, delegacion10_.tipovia as tipovia39_8_, delegacion10_.nomvia as nomvia39_8_, delegacion10_.numero as numero39_8_, delegacion10_.otrdir as otrdir39_8_, delegacion10_.localidad as locali7_39_8_, delegacion10_.telefono as telefono39_8_, delegacion10_.provincia as provin9_39_8_, tipovia11_.cdg as cdg21_9_, tipovia11_.descripcion as descri2_21_9_, provincia12_.cdg as cdg55_10_, provincia12_.descripcion as descri2_55_10_, provincia12_.compro as compro55_10_, admon13_.cdg as cdg70_11_, admon13_.descripcion as descri2_70_11_, documento14_.cdg as cdg14_12_, documento14_.descripcion as descri2_14_12_, documento15_.cdg as cdg14_13_, documento15_.descripcion as descri2_14_13_, empresario16_.cdg as cdg15_14_, empresario16_.descripcion as descri2_15_14_, pais17_.cdg as cdg54_15_, pais17_.descripcion as descri2_54_15_, pais18_.cdg as cdg54_16_, pais18_.descripcion as descri2_54_16_, persona19_.cdg as cdg96_17_, persona19_.alias as alias96_17_, persona19_.descripcion as descri3_96_17_, persona19_.codpos as codpos96_17_, persona19_.fecnew as fecnew96_17_, persona19_.hornew as hornew96_17_, persona19_.fecmod as fecmod96_17_, persona19_.hormod as hormod96_17_, persona19_.numdoc as numdoc96_17_, persona19_.fecnac as fecnac96_17_, persona19_.sexo as sexo96_17_, persona19_.inddoc as inddoc96_17_, persona19_.paiemi as paiemi96_17_, persona19_.painac as painac96_17_, persona19_.pronac as pronac96_17_, persona19_.tipovia as tipovia96_17_, persona19_.apellido2 as apellido12_96_17_, persona19_.nombre as nombre96_17_, persona19_.aliastc2 as aliastc14_96_17_, persona19_.nomvia as nomvia96_17_, persona19_.numero as numero96_17_, persona19_.otrdir as otrdir96_17_, persona19_.localidad as local18_96_17_, persona19_.telefono as telefono96_17_, persona19_.fax as fax96_17_, persona19_.email as email96_17_, persona19_.lugnac as lugnac96_17_, persona19_.padre as padre96_17_, persona19_.madre as madre96_17_, persona19_.numss as numss96_17_, persona19_.estciv as estciv96_17_, persona19_.obsper as obsper96_17_, persona19_.nacion as nacion96_17_, persona19_.provincia as provi32_96_17_, documento20_.cdg as cdg14_18_, documento20_.descripcion as descri2_14_18_, pais21_.cdg as cdg54_19_, pais21_.descripcion as descri2_54_19_, pais22_.cdg as cdg54_20_, pais22_.descripcion as descri2_54_20_, provincia23_.cdg as cdg55_21_, provincia23_.descripcion as descri2_55_21_, provincia23_.compro as compro55_21_, tipovia24_.cdg as cdg21_22_, tipovia24_.descripcion as descri2_21_22_, nacion25_.cdg as cdg53_23_, nacion25_.descripcion as descri2_53_23_, provincia26_.cdg as cdg55_24_, provincia26_.descripcion as descri2_55_24_, provincia26_.compro as compro55_24_ 
			from otrperc this_, emprnif empresa2_, outer (cliente cliente3_, outer divisa divisa4_, outer tipdoc documento5_, outer tipempr empresario6_, outer pais pais7_, outer tipovia tipovia8_, outer provincia provincia9_, outer (delegacion delegacion10_, outer tipovia tipovia11_, outer provincia provincia12_)), outer admon admon13_, outer tipdoc documento14_, outer tipdoc documento15_, outer tipempr empresario16_, outer pais pais17_, outer pais pais18_, persona persona19_, outer tipdoc documento20_, outer pais pais21_, outer pais pais22_, outer provincia provincia23_, outer tipovia tipovia24_, outer nacion nacion25_, outer provincia provincia26_ 
			where this_.codemp = empresa2_.cdg and empresa2_.codcli = cliente3_.cdg and cliente3_.divisa = divisa4_.cdg and cliente3_.inddoc = documento5_.cdg and cliente3_.tipemp = empresario6_.cdg and cliente3_.paiemi = pais7_.cdg and cliente3_.tipovia = tipovia8_.cdg and cliente3_.provincia = provincia9_.cdg and cliente3_.coddlg = delegacion10_.cdg and delegacion10_.tipovia = tipovia11_.cdg and delegacion10_.provincia = provincia12_.cdg and empresa2_.codadm = admon13_.cdg and empresa2_.inddoc = documento14_.cdg and empresa2_.tipdocrep = documento15_.cdg and empresa2_.tipempr = empresario16_.cdg and empresa2_.paiemi = pais17_.cdg and empresa2_.paidocrep = pais18_.cdg and this_.codper = persona19_.cdg and persona19_.inddoc = documento20_.cdg and persona19_.paiemi = pais21_.cdg and persona19_.painac = pais22_.cdg and persona19_.pronac = provincia23_.cdg and persona19_.tipovia = tipovia24_.cdg and persona19_.nacion = nacion25_.cdg and persona19_.provincia = provincia26_.cdg 
			order by this_.cdg asc limit 20
			*/
			
			
			StringWriter stmt = new StringWriter();
			
//			stmt.append("select this_.cdg as cdg97_25_, this_.codemp as codemp97_25_, this_.codper as codper97_25_, this_.fecha as fecha97_25_, this_.base as base97_25_, this_.importe as importe97_25_, this_.anio as anio97_25_, this_.concepto as concepto97_25_, this_.clave as clave97_25_, this_.prcret as prcret97_25_, this_.retencion as retenc9_97_25_, this_.aporta_ss as aporta10_97_25_, this_.ingreso as ingreso97_25_, this_.subclave as subclave97_25_, this_.natret as natret97_25_, empresa2_.cdg as cdg75_0_, empresa2_.alias as alias75_0_, empresa2_.descripcion as descri3_75_0_, empresa2_.codcli as codcli75_0_, empresa2_.fecini as fecini75_0_, empresa2_.fecfin as fecfin75_0_, empresa2_.indcal as indcal75_0_, empresa2_.indnom as indnom75_0_, empresa2_.indcoste as indcoste75_0_, empresa2_.fecnew as fecnew75_0_, empresa2_.hornew as hornew75_0_, empresa2_.fecmod as fecmod75_0_, empresa2_.hormod as hormod75_0_, empresa2_.envioss as envioss75_0_, empresa2_.numdoc as numdoc75_0_, empresa2_.representante as repre15_75_0_, empresa2_.cargo as cargo75_0_, empresa2_.fecnac as fecnac75_0_, empresa2_.nrodocrep as nrodo18_75_0_, empresa2_.sexo as sexo75_0_, empresa2_.feccon as feccon75_0_, empresa2_.obsnif as obsnif75_0_, empresa2_.datreg as datreg75_0_, empresa2_.indirpf as indirpf75_0_, empresa2_.cecon as cecon75_0_, empresa2_.modimpuesto as modim25_75_0_, empresa2_.codadm as codadm75_0_, empresa2_.divisa as divisa75_0_, empresa2_.inddoc as inddoc75_0_, empresa2_.tipdocrep as tipdo29_75_0_, empresa2_.tipempr as tipempr75_0_, empresa2_.paiemi as paiemi75_0_, empresa2_.paidocrep as paido33_75_0_, persona19_.cdg as cdg96_17_, persona19_.alias as alias96_17_, persona19_.descripcion as descri3_96_17_, persona19_.codpos as codpos96_17_, persona19_.fecnew as fecnew96_17_, persona19_.hornew as hornew96_17_, persona19_.fecmod as fecmod96_17_, persona19_.hormod as hormod96_17_, persona19_.numdoc as numdoc96_17_, persona19_.fecnac as fecnac96_17_, persona19_.sexo as sexo96_17_, persona19_.inddoc as inddoc96_17_, persona19_.paiemi as paiemi96_17_, persona19_.painac as painac96_17_, persona19_.pronac as pronac96_17_, persona19_.tipovia as tipovia96_17_, persona19_.apellido2 as apellido12_96_17_, persona19_.nombre as nombre96_17_, persona19_.aliastc2 as aliastc14_96_17_, persona19_.nomvia as nomvia96_17_, persona19_.numero as numero96_17_, persona19_.otrdir as otrdir96_17_, persona19_.localidad as local18_96_17_, persona19_.telefono as telefono96_17_, persona19_.fax as fax96_17_, persona19_.email as email96_17_, persona19_.lugnac as lugnac96_17_, persona19_.padre as padre96_17_, persona19_.madre as madre96_17_, persona19_.numss as numss96_17_, persona19_.estciv as estciv96_17_, persona19_.obsper as obsper96_17_, persona19_.nacion as nacion96_17_, persona19_.provincia as provi34_96_17_ " +
//					" from otrperc this_, emprnif empresa2_,cliente cliente3_) " +
//					" where this_.codemp = empresa2_.cdg and empresa2_.codcli = cliente3_.cdg" +
//					" order by this_.cdg asc limit 20");
			
			stmt.append("select cdg, fecha, concepto, clave,importe,base,prcret,retencion,aporta_ss,anio,ingreso,subclave,natret,codemp,codper " +
					"from otrperc");
			
			
			/*
			stmt.append("SELECT *");
			stmt.append(" SUM(id.taxable_base)");
			stmt.append(" FROM (otrperc op  ");
			stmt.append(" INNER JOIN emprnif emp ON (op.codemp = emp.cdg))");
			stmt.append(" INNER JOIN persona per ON (op.codper = per.cdg)");
			stmt.append(" WHERE i.type = 1");
			
			stmt.append(" GROUP BY YEAR(i.issue_date)");
			stmt.append(" ORDER BY op.cdg ASC");
			*/

			ps = HibernateUtil.getSQLConnection().prepareStatement(
					stmt.toString(), ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);
			
			
			rs = ps.executeQuery();
			
			List<Otrperc> percs = new LinkedList<Otrperc>();
			while (rs.next()) {
				Otrperc perc = new Otrperc();
				/*
				perc.setKey(rs.getInt(1));
				perc.setName(rs.getString(1));
				int count = rs.getInt(2);
				double amount = rs.getInt(3);
				perc.setNumInvoice(count);
				perc.setAmount(amount);
				perc.setAverageAmount(CommonUtil.round(amount / count));
				*/
				
				
				
				
				perc.setCdg(rs.getInt(1));
				perc.setFecha(rs.getDate(2));
				perc.setConcepto(rs.getString(3));
				String clave = rs.getString(4);
				//perc.setClave(Claveper.valueOf(clave));
				perc.setImporte(rs.getBigDecimal(5));
				perc.setBase(rs.getBigDecimal(6));
			    perc.setPrcret(rs.getBigDecimal(7));
			    perc.setRetencion(rs.getBigDecimal(8));
				perc.setAportaSs(rs.getBigDecimal(9));
				perc.setAnio(rs.getInt(10));
				String ingreso = rs.getString(11);
			    //perc.setIngreso(Ingreso.valueOf(ingreso));
			    perc.setSubclave(rs.getString(12));
			    String retrib = rs.getString(13);
			    //perc.setNatret(Retribuciones.valueOf(retrib));
			    perc.setEmpresa(new Empresa());
			    perc.getEmpresa().setCdg(rs.getInt(14));
			    perc.setPersona(new Persona());
			    perc.getPersona().setCdg(rs.getInt(15));
			    
				
				
				/*
				private Integer cdg;
			     private Date fecha;
			     private String concepto;
			     private Claveper clave;
			     private BigDecimal importe;
			     private BigDecimal base;
			     private BigDecimal prcret;
			     private BigDecimal retencion;
			     private BigDecimal aportaSs;
			     private Integer anio;
			     private Ingreso ingreso;
			     private String subclave;
			     private Retribuciones natret;
			     private Empresa empresa;
			     private Persona persona;
			     */
			     percs.add(perc);
			}
			return percs;
			
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
	
	
	
	
}
