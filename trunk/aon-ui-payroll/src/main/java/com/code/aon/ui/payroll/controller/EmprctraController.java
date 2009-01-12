package com.code.aon.ui.payroll.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.payroll.auxiliares.convenios.Convenio;
import com.code.aon.payroll.dao.IPayrollAlias;
import com.code.aon.payroll.enumeration.EnvioSS2;
import com.code.aon.payroll.enumeration.Inddias;
import com.code.aon.payroll.enumeration.Representantes;
import com.code.aon.payroll.principales.Domicilio;
import com.code.aon.payroll.principales.empresa.Actividad;
import com.code.aon.payroll.principales.empresa.Emprctra;
import com.code.aon.payroll.principales.empresa.Empresa;
import com.code.aon.ui.form.LinesController;

public class EmprctraController extends LinesController {


	
	private List<SelectItem> listaenvioss;
	private List<SelectItem> inndia;
	private List<SelectItem> represen;	
	private Actividad actividad;
	private Empresa empresa;
	private Domicilio domicilio;
	private Convenio convenio;
	private Boolean toxicos;
    private Boolean indcal;
    private Boolean indnom;
    private Boolean indcoste;
	
    
	/**
	 * genera un cdg siguiendo al maximo 
	 */
	public void generateCdg(){
		((Emprctra)getTo()).getId().setCdg(Integer.parseInt(Utils.maxCode("Emprctra", "cdg"))+1);
	}
	
    
    
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
	
	
	
	
	
	public List<SelectItem> getListainddia() {
		if(inndia==null){
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			inndia = new LinkedList<SelectItem>();
			for (Inddias e : Inddias.values()) {
				String name = e.getName( locale );
				SelectItem item = new SelectItem( e, name );
				inndia.add(item);
			}
		}
		return inndia;
	}



	
	public List<SelectItem> getListarepre() {
		if(represen==null){
			
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			represen = new LinkedList<SelectItem>();
			for (Representantes e : Representantes.values()) {
				String name = e.getName( locale );
				SelectItem item = new SelectItem( e, name );
				represen.add(item);
			}
		}
		return represen;
	}
	
	

	@Override
	   public void onEditSearch(ActionEvent arg0) {
		   super.onEditSearch(arg0);
		   
	       setActividad( new Actividad() );
	       setEmpresa( new Empresa() );
	       setConvenio( new Convenio() );
	       setDomicilio( new Domicilio() );
		   }
		
	
	
		@Override
		public void onSearch(ActionEvent event) {
			

			try {
			if (empresa.getCdg() != null) {
				getCriteria().addEqualExpression(
						getFieldName(IPayrollAlias.EMPRCTRA_EMPRESA_CDG),
						getEmpresa().getCdg());
			}
			if (actividad.getCdg() != null) {
				getCriteria().addEqualExpression(
						getFieldName(IPayrollAlias.EMPRCTRA_ACTIVIDAD_CDG),
						getActividad().getCdg());
			}
			
			if (domicilio.getCdg() != null) {
				getCriteria().addEqualExpression(
						getFieldName(IPayrollAlias.EMPRCTRA_DOMICILIO_CDG),
						getDomicilio().getCdg());
			}

			if ((convenio.getCdg() != null) &&  (!StringUtils.isEmpty(convenio.getCdg())) ) {
				getCriteria().addEqualExpression(
						getFieldName(IPayrollAlias.EMPRCTRA_CONVENIO_CDG),
						getConvenio().getCdg());
			}


			  
			if(toxicos){
				
				getCriteria().addEqualExpression(
						getFieldName(IPayrollAlias.EMPRCTRA_TOXICOS), true);
			}

			if (indcal) {

				getCriteria().addEqualExpression(
						getFieldName(IPayrollAlias.EMPRCTRA_INDCAL), true);
			}
			
			

			if (indcoste) {

				getCriteria().addEqualExpression(
						getFieldName(IPayrollAlias.EMPRCTRA_INDCOSTE), true);
			}
			
			

			if (indnom) {

				getCriteria().addEqualExpression(
						getFieldName(IPayrollAlias.EMPRCTRA_INDNOM), true);
			}
			

			} catch (ManagerBeanException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
	    toxicos = false;
	    indcal = false;
	    indcoste = false;
	    indnom = false;
			
		
			
			super.onSearch(event);
		}


		public Actividad getActividad() {
			return actividad;
		}


		public void setActividad(Actividad actividad) {
			this.actividad = actividad;
		}


		public Empresa getEmpresa() {
			return empresa;
		}


		public void setEmpresa(Empresa empresa) {
			this.empresa = empresa;
		}


		public Domicilio getDomicilio() {
			return domicilio;
		}


		public void setDomicilio(Domicilio domicilio) {
			this.domicilio = domicilio;
		}


		public Convenio getConvenio() {
			return convenio;
		}


		public void setConvenio(Convenio convenio) {
			this.convenio = convenio;
		}


		public Boolean getToxicos() {
			return toxicos;
		}


		public void setToxicos(Boolean toxicos) {
			this.toxicos = toxicos;
		}


		public Boolean getIndcal() {
			return indcal;
		}


		public void setIndcal(Boolean indcal) {
			this.indcal = indcal;
		}


		public Boolean getIndnom() {
			return indnom;
		}


		public void setIndnom(Boolean indnom) {
			this.indnom = indnom;
		}


		public Boolean getIndcoste() {
			return indcoste;
		}


		public void setIndcoste(Boolean indcoste) {
			this.indcoste = indcoste;
		}


	
}
