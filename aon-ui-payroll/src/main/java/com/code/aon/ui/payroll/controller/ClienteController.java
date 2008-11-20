package com.code.aon.ui.payroll.controller;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;


import com.code.aon.common.ManagerBeanException;
import com.code.aon.payroll.auxiliares.organismosyentidades.Delegacion;
import com.code.aon.payroll.dao.IPayrollAlias;
import com.code.aon.payroll.divisa.Divisa;
import com.code.aon.payroll.enumeration.EnvioSS;
import com.code.aon.payroll.geograficas.Pais;
import com.code.aon.payroll.geograficas.Provincia;
import com.code.aon.payroll.principales.Cliente;
import com.code.aon.payroll.tipos.Documento;
import com.code.aon.payroll.tipos.Empresario;
import com.code.aon.payroll.tipos.Tipovia;


public class ClienteController extends PayrollBasicController {

	
	private List<SelectItem> envioss;
	
	private Delegacion delegacion;
	private Empresario empresario;
	private Documento documento;
	private Divisa divisa;	
	private Tipovia tipovia;
	private Provincia provincia;
    private Pais pais;
	private Date fecnew;
	private Date hornew;
    private boolean inactivo;
	private boolean indcal;
	private boolean indnom;
	private boolean indcoste;
	private boolean soloases;
	/**
	 * Recupera los tipos de retribuciones 
	 * 
	 * @return
	 */
	public List<SelectItem> getListaEnvioss() {
		if(envioss==null){
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			envioss = new LinkedList<SelectItem>();
			for (EnvioSS p : EnvioSS.values()) {
				String name = p.getName( locale );
				SelectItem item = new SelectItem( p, name );
				envioss.add(item);
			}
		}
		return envioss;
	}

	

		@Override
	   public void onEditSearch(ActionEvent arg0) {
		   super.onEditSearch(arg0);
		   
	       setTipovia( new Tipovia() );
		   setProvincia( new Provincia() );
		   setDelegacion( new Delegacion() );
		   setEmpresario( new Empresario() );
		   setDocumento( new Documento() );
		   setDivisa( new Divisa() );
		   setPais( new Pais() );
		  

	}
		
		@Override
		public void onSearch(ActionEvent event) {
			

			try {
				if   (delegacion.getCdg() != null)  {
					getCriteria().addEqualExpression(getFieldName(IPayrollAlias.CLIENTE_DELEGACION_CDG), getDelegacion().getCdg());
				}
				if  ( (empresario.getCdg() != null) && (! StringUtils.isEmpty(empresario.getCdg())) ) {
					getCriteria().addEqualExpression(getFieldName(IPayrollAlias.CLIENTE_TIPEMPR_CDG), getEmpresario().getCdg());
				}
				
				if  ( (documento.getCdg() != null) && (! StringUtils.isEmpty(documento.getCdg())) ) {
					getCriteria().addEqualExpression(getFieldName(IPayrollAlias.CLIENTE_TIPDOC_CDG), getDocumento().getCdg());
				}
				
				if  ( (divisa.getCdg() != null) && (! StringUtils.isEmpty(divisa.getCdg())) ) {
					getCriteria().addEqualExpression(getFieldName(IPayrollAlias.CLIENTE_DIVISA_CDG), getDivisa().getCdg());
				}
				
			    if  ( (tipovia.getCdg() != null) && (! StringUtils.isEmpty(tipovia.getCdg())) ) {
					getCriteria().addEqualExpression(getFieldName(IPayrollAlias.CLIENTE_TIPOVIA_CDG), getTipovia().getCdg());
				}
				
			     if  ( (provincia.getCdg() != null) && (! StringUtils.isEmpty(provincia.getCdg())) ) {
					getCriteria().addEqualExpression(getFieldName(IPayrollAlias.CLIENTE_PROVINCIA_CDG), getProvincia().getCdg());
				}
			     if  ( (pais.getCdg() != null) && (! StringUtils.isEmpty(pais.getCdg())) ) {
						getCriteria().addEqualExpression(getFieldName(IPayrollAlias.CLIENTE_PAIS_CDG), getProvincia().getCdg());
					}
			     
			    if    (fecnew != null){
						getCriteria().addEqualExpression(getFieldName(IPayrollAlias.CLIENTE_FECNEW), getFecnew());
					}
			    
				if(hornew != null){
					
						getCriteria().addEqualExpression(getFieldName(IPayrollAlias.CLIENTE_HORNEW), getHornew());
					}
				
				if(inactivo){
					
					getCriteria().addEqualExpression(getFieldName(IPayrollAlias.CLIENTE_INACTIVO),true);
				}
				if(indcal){
					getCriteria().addEqualExpression(getFieldName(IPayrollAlias.CLIENTE_INDCAL), true);
				}
				if(indnom){
					getCriteria().addEqualExpression(getFieldName(IPayrollAlias.CLIENTE_INDNOM),true);
				}
				if(indcoste){
					getCriteria().addEqualExpression(getFieldName(IPayrollAlias.CLIENTE_INDCOSTE),true);
				}
				if(soloases){
					getCriteria().addEqualExpression(getFieldName(IPayrollAlias.CLIENTE_SOLOASES),true);
				}
			} catch (ManagerBeanException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			inactivo=false;
			indcal=false;
			indnom=false;
			indcoste=false;
			soloases=false;
			
			super.onSearch(event);
		}



		public Tipovia getTipovia() {
			return tipovia;
		}

		public void setTipovia(Tipovia tipovia) {
			this.tipovia = tipovia;
		}

		public Provincia getProvincia() {
			return provincia;
		}

		public void setProvincia(Provincia provincia) {
			this.provincia = provincia;
		}



		public Delegacion getDelegacion() {
			return delegacion;
		}



		public void setDelegacion(Delegacion delegacion) {
			this.delegacion = delegacion;
		}



		public Empresario getEmpresario() {
			return empresario;
		}



		public void setEmpresario(Empresario empresario) {
			this.empresario = empresario;
		}



		public Documento getDocumento() {
			return documento;
		}



		public void setDocumento(Documento documento) {
			this.documento = documento;
		}



		public Divisa getDivisa() {
			return divisa;
		}



		public void setDivisa(Divisa divisa) {
			this.divisa = divisa;
		}



		public Pais getPais() {
			return pais;
		}



		public void setPais(Pais pais) {
			this.pais = pais;
		}



		public Date getFecnew() {
			return fecnew;
		}



		public void setFecnew(Date fecnew) {
			this.fecnew = fecnew;
		}



		public Date getHornew() {
			return hornew;
		}



		public void setHornew(Date hornew) {
			this.hornew = hornew;
		}



		public boolean getInactivo() {
			return inactivo;
		}



		public void setInactivo(boolean inactivo) {
			this.inactivo = inactivo;
		}



		public boolean getIndcal() {
			return indcal;
		}



		public void setIndcal(boolean indcal) {
			this.indcal = indcal;
		}



		public boolean getIndnom() {
			return indnom;
		}



		public void setIndnom(boolean indnom) {
			this.indnom = indnom;
		}



		public boolean getIndcoste() {
			return indcoste;
		}



		public void setIndcoste(boolean indcoste) {
			this.indcoste = indcoste;
		}



		public boolean getSoloases() {
			return soloases;
		}



		public void setSoloases(boolean soloases) {
			this.soloases = soloases;
		}




	}








