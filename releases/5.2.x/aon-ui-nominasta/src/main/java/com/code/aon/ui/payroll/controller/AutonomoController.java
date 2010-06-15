package com.code.aon.ui.payroll.controller;

import java.util.Date;
import com.code.aon.payroll.enumeration.TipAutonomo;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.payroll.auxiliares.organismosyentidades.Mutua;
import com.code.aon.payroll.auxiliares.organismosyentidades.Sucursal;
import com.code.aon.payroll.dao.IPayrollAlias;
import com.code.aon.payroll.geograficas.Provincia;
import com.code.aon.payroll.principales.autonomos.Autonomos;
import com.code.aon.payroll.principales.personas.Persona;
import com.code.aon.payroll.tipos.Tipovia;

public class AutonomoController extends PayrollBasicController {

	
	
	private Persona persona;
	private Mutua mutua;
	private Tipovia tipovia1;
	private Tipovia tipovia2;
	private Provincia provincia1;
	private Provincia provincia2;
	private Sucursal sucursal1;
	private Sucursal sucursal2;	
	private Date fecinigestion;
	private Date fecfingestion;
	private Date fecconstitucion;
	private Date fecalta;
	private boolean incremento;
	private boolean incapacidad;
	
	@Override
	   public void onEditSearch(ActionEvent arg0) {
		   super.onEditSearch(arg0);
		   
	       setTipovia1( new Tipovia() );
		   setProvincia1( new Provincia() );
		   setTipovia2( new Tipovia() );
		   setProvincia2( new Provincia() );
		   setPersona( new Persona() );
		   setMutua( new Mutua() );
	
		   }
		
		@Override
		public void onSearch(ActionEvent event) {
			

			try {
				if   (tipovia1.getCdg() != null && (! StringUtils.isEmpty(tipovia1.getCdg())))  {
					getCriteria().addEqualExpression(getFieldName(IPayrollAlias.AUTONOMOS_TIPOVIA_CDG),getTipovia1().getCdg());
				}
				if   (tipovia2.getCdg() != null && (! StringUtils.isEmpty(tipovia2.getCdg())))  {
					getCriteria().addEqualExpression(getFieldName(IPayrollAlias.AUTONOMOS_PERSONA_TIPOVIA_CDG), getTipovia2().getCdg());
				}
				if   (provincia1.getCdg() != null && (! StringUtils.isEmpty(provincia1.getCdg())))  {
					getCriteria().addEqualExpression(getFieldName(IPayrollAlias.AUTONOMOS_PROVINCIA_CDG), getProvincia1().getCdg());
				}
				if   (provincia2.getCdg() != null && (! StringUtils.isEmpty(provincia2.getCdg())))  {
					getCriteria().addEqualExpression(getFieldName(IPayrollAlias.AUTONOMOS_PERSONA_PROVINCIA_CDG), getProvincia2().getCdg());
				}
				if   (mutua.getCdg() != null && (! StringUtils.isEmpty(mutua.getCdg())))  {
					getCriteria().addEqualExpression(getFieldName(IPayrollAlias.AUTONOMOS_MUTUA_CDG), getMutua().getCdg());
				}
				if   (persona.getCdg() != null)  {
					getCriteria().addEqualExpression(getFieldName(IPayrollAlias.AUTONOMOS_PERSONA_CDG), getPersona().getCdg());
				}
				/*	if   (sucursal1.getId().getCdg() != null)  {
					getCriteria().addEqualExpression(getFieldName(IPayrollAlias.AUTONOMOS_SUCURSAL1_ID_CDG), getSucursal1().getId().getCdg());
				}
				
				if   (sucursal2.getEntidad().getCdg()!= null )  {
					getCriteria().addEqualExpression(getFieldName(IPayrollAlias.CLIENTE_DELEGACION_CDG), getDelegacion().getCdg());
				}*/
		
			     
			    if    (fecinigestion != null){
						getCriteria().addEqualExpression(getFieldName(IPayrollAlias.AUTONOMOS_FECINIGESTION), getFecinigestion());
					}
			    if    (fecfingestion != null){
					getCriteria().addEqualExpression(getFieldName(IPayrollAlias.AUTONOMOS_FECFINGESTION), getFecfingestion());
				}
			    if    (fecconstitucion != null){
					getCriteria().addEqualExpression(getFieldName(IPayrollAlias.AUTONOMOS_FECCONSTITUCION), getFecconstitucion());
				}
			    if    (fecalta != null){
					getCriteria().addEqualExpression(getFieldName(IPayrollAlias.AUTONOMOS_FECALTA), getFecalta());
				}
			    if(incremento)
					getCriteria().addEqualExpression(getFieldName(IPayrollAlias.AUTONOMOS_INCREMENTO),"N");
				if(incapacidad)
					getCriteria().addEqualExpression(getFieldName(IPayrollAlias.AUTONOMOS_INCAPACIDAD),"N");
			 
			    

			} catch (ManagerBeanException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			incremento=false;
			incapacidad	=false;
			
			super.onSearch(event);
		}

		public Persona getPersona() {
			return persona;
		}

		public void setPersona(Persona persona) {
			this.persona = persona;
		}

		public Mutua getMutua() {
			return mutua;
		}

		public void setMutua(Mutua mutua) {
			this.mutua = mutua;
		}

		public Tipovia getTipovia1() {
			return tipovia1;
		}

		public void setTipovia1(Tipovia tipovia1) {
			this.tipovia1 = tipovia1;
		}

		public Tipovia getTipovia2() {
			return tipovia2;
		}

		public void setTipovia2(Tipovia tipovia2) {
			this.tipovia2 = tipovia2;
		}

		public Provincia getProvincia1() {
			return provincia1;
		}

		public void setProvincia1(Provincia provincia1) {
			this.provincia1 = provincia1;
		}

		public Provincia getProvincia2() {
			return provincia2;
		}

		public void setProvincia2(Provincia provincia2) {
			this.provincia2 = provincia2;
		}

		public Sucursal getSucursal1() {
			return sucursal1;
		}

		public void setSucursal1(Sucursal sucursal1) {
			this.sucursal1 = sucursal1;
		}

		public Sucursal getSucursal2() {
			return sucursal2;
		}

		public void setSucursal2(Sucursal sucursal2) {
			this.sucursal2 = sucursal2;
		}

		public Date getFecinigestion() {
			return fecinigestion;
		}

		public void setFecinigestion(Date fecinigestion) {
			this.fecinigestion = fecinigestion;
		}

		public Date getFecfingestion() {
			return fecfingestion;
		}

		public void setFecfingestion(Date fecfingestion) {
			this.fecfingestion = fecfingestion;
		}

		public Date getFecconstitucion() {
			return fecconstitucion;
		}

		public void setFecconstitucion(Date fecconstitucion) {
			this.fecconstitucion = fecconstitucion;
		}

		public Date getFecalta() {
			return fecalta;
		}

		public void setFecalta(Date fecalta) {
			this.fecalta = fecalta;
		}

		public boolean isIncremento() {
			return incremento;
		}

		public void setIncremento(boolean incremento) {
			this.incremento = incremento;
		}

		public boolean isIncapacidad() {
			return incapacidad;
		}

		public void setIncapacidad(boolean incapacidad) {
			this.incapacidad = incapacidad;
		}

	
		private List<SelectItem> tipautonomos;
		
		
		
		public List<SelectItem> getListaAutonomos() {
			if(tipautonomos==null){
				Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
				tipautonomos = new LinkedList<SelectItem>();				
				for (TipAutonomo ia : TipAutonomo.values()) {
					String name = ia.getName( locale );
					SelectItem item = new SelectItem( ia, name );
					tipautonomos.add(item);
				}
			}
			return tipautonomos;
		}
		
		Integer code ;
	    public Integer getCode() throws ManagerBeanException {	
	    	
	    	
		       	code = 0;		
				String consulta = "select max(cdg) from Autonomos";			
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
	    
	    
	    
	    
		public void verifyNullFields(){
			if (StringUtils.isEmpty(((Autonomos)getTo()).getTipovia().getCdg()))
			((Autonomos) getTo()).getTipovia().setCdg("CL");

		if (StringUtils.isEmpty(((Autonomos)getTo()).getMutua().getCdg()))
			((Autonomos) getTo()).setMutua(null);

	//	if (StringUtils.isEmpty(((Autonomos)getTo()).getProvincia().getCdg()))
	//		((Autonomos) getTo()).setProvincia(null);

		if (StringUtils.isEmpty(((Autonomos)getTo()).getTipovia().getCdg()))
			((Autonomos) getTo()).setTipovia(null);
	}
		
		public void setDefaultFields(){		
			Autonomos  c = (Autonomos)getTo();		
			c.getTipovia().setCdg("CL");
		

		}
	    
	    
	    
	    
	    
	    
}
