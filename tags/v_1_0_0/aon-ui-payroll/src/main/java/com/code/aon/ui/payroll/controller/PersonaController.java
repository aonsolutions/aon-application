package com.code.aon.ui.payroll.controller;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.payroll.dao.IPayrollAlias;
import com.code.aon.payroll.enumeration.EstadoCivil;
import com.code.aon.payroll.enumeration.Sexo;
import com.code.aon.payroll.geograficas.Nacion;
import com.code.aon.payroll.geograficas.Pais;
import com.code.aon.payroll.geograficas.Provincia;
import com.code.aon.payroll.principales.personas.Persona;
import com.code.aon.payroll.tipos.Documento;
import com.code.aon.payroll.tipos.Tipovia;


public class PersonaController extends PayrollBasicController {
	
	private List<SelectItem> sexo;
	private List<ITransferObject> selectedList;
	/**
	 * Recupera los tipos de sexo 
	 * 
	 * @return
	 */
	public List<SelectItem> getListaSexo() {
		if(sexo==null){
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			sexo = new LinkedList<SelectItem>();
			for (Sexo p : Sexo.values()) {
				String name = p.getName( locale );
				SelectItem item = new SelectItem( p, name );
				sexo.add(item);
			}
		}
		return sexo;
	}
	
	@Override
	public void onSelect(ActionEvent event) {
		selectedList = new LinkedList<ITransferObject>();
		super.onSelect(event);		
		selectedList.add(this.getTo());		
						
	}

	
	private List<SelectItem> estciv;
	/**
	 * Recupera los tipos de sexo 
	 * 
	 * @return
	 */
	public List<SelectItem> getListaEstciv() {
		if(sexo==null){
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			estciv = new LinkedList<SelectItem>();
			for (EstadoCivil p : EstadoCivil.values()) {
				String name = p.getName( locale );
				SelectItem item = new SelectItem( p, name );
				estciv.add(item);
			}
		}
		return estciv;
	}
	

	private Documento tipdoc;
    private Nacion nacion;
    private Pais pais;
    private Pais pais1;
    private Provincia provincia;
    private Provincia provincia1;
    private Tipovia tipovia;
    private Date fecnac;
    
	public Documento getTipdoc() {
		return tipdoc;
	}
	public void setTipdoc(Documento tipdoc) {
		this.tipdoc = tipdoc;
	}
	public Nacion getNacion() {
		return nacion;
	}
	public void setNacion(Nacion nacion) {
		this.nacion = nacion;
	}
	public Pais getPais() {
		return pais;
	}
	public void setPais(Pais pais) {
		this.pais = pais;
	}
	public Pais getPais1() {
		return pais1;
	}
	public void setPais1(Pais pais1) {
		this.pais1 = pais1;
	}
	public Provincia getProvincia() {
		return provincia;
	}
	public void setProvincia(Provincia provincia) {
		this.provincia = provincia;
	}
	public Provincia getProvincia1() {
		return provincia1;
	}
	public void setProvincia1(Provincia provincia1) {
		this.provincia1 = provincia1;
	}
	public Tipovia getTipovia() {
		return tipovia;
	}
	public void setTipovia(Tipovia tipovia) {
		this.tipovia = tipovia;
	}
	public Date getFecnac() {
		return fecnac;
	}
	public void setFecnac(Date fecnac) {
		this.fecnac = fecnac;
	}
	
	
	@Override
	public void onEditSearch(ActionEvent arg0) {
		
		tipdoc = new Documento();
		nacion = new Nacion();
		pais = new Pais();
		pais1 = new Pais();
		provincia = new Provincia();
		provincia1 = new Provincia();
		tipovia = new Tipovia();
		tabName = null;
		
		super.onEditSearch(arg0);
		
	}
	
	/**
	 * Se incluyen manualmente a las búsquedas los campos lookup y de fechas 
	 */
	@Override
	public void onSearch(ActionEvent event) {
		try {
			
			/*
			 * Búsqueda por campos lookup
			 */
			if ((tipdoc.getCdg() != null) && (! StringUtils.isEmpty(tipdoc.getCdg()))) {
				getCriteria().addEqualExpression(getFieldName(IPayrollAlias.EMPRESA_TIPDOC_CDG), tipdoc.getCdg());
			}
			if ((nacion.getCdg() != null) && (! StringUtils.isEmpty(nacion.getCdg()))) {
				getCriteria().addEqualExpression(getFieldName(IPayrollAlias.PERSONA_NACION_CDG), nacion.getCdg());
			}
			if ((pais.getCdg() != null) && (! StringUtils.isEmpty(pais.getCdg()))) {
				getCriteria().addEqualExpression(getFieldName(IPayrollAlias.EMPRESA_PAIS_CDG), pais.getCdg());
			}
			if ((pais1.getCdg() != null) && (! StringUtils.isEmpty(pais1.getCdg()))) {
				getCriteria().addEqualExpression(getFieldName(IPayrollAlias.EMPRESA_PAIS1_CDG), pais1.getCdg());
			}
			if ((provincia.getCdg() != null) && (! StringUtils.isEmpty(provincia.getCdg()))) {
				getCriteria().addEqualExpression(getFieldName(IPayrollAlias.PERSONA_PROVINCIA_CDG), provincia.getCdg());
			}
			if ((provincia1.getCdg() != null) && (! StringUtils.isEmpty(provincia1.getCdg()))) {
				getCriteria().addEqualExpression(getFieldName(IPayrollAlias.PERSONA_PROVINCIA1_CDG), provincia1.getCdg());
			}
			if ((tipovia.getCdg() != null) && (! StringUtils.isEmpty(tipovia.getCdg()))) {
				getCriteria().addEqualExpression(getFieldName(IPayrollAlias.PERSONA_TIPOVIA_CDG), tipovia.getCdg());
			}
			
			/*
			 * Búsqueda por campos Date
			 */
			if (fecnac != null) {
				getCriteria().addEqualExpression(getFieldName(IPayrollAlias.PERSONA_FECNAC), fecnac);
			}
			
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		super.onSearch(event);
		
		fecnac = null;
	}
	
	/**
	 * Metodo que asigna el asliasTc2 discriminando los DE, LA, DEL, e Y.
	 */
	public void ponerAliasTc2(){
	/*private function lFnPonerAliasTC2
	objects begin
	    lChAp1
	    lChAp2
	    lChNom      as char
	    
	    lBolBreak   as boolean
	    
	    lSmI        as smallint
	end
	begin
	    lChAp1 = TxtApellido1.Text.Trim;
	    lChAp2 = TxtApellido2.Text.Trim;
	    lChNom = TxtNombre.Text.Trim;
	    
	    lBolBreak = false;
	    
	    if lFnEstaVacio(lChAp1) then
	        lChAp1 = "  ";
	    else begin
	        if lChAp1.Length < 2 then
	            lChAp1 += " ".StrRepeat(2 - lChAp1.Length);
	        else
	            while (lSmI = lChAp1.Locate(" ")) > 0 and not lBolBreak do
	                if lChAp1[1, lSmI - 1] in ("DE", "LA", "DEL", "Y") then
	                    lChAp1 = lChAp1[lSmI + 1, lChAp1.Length - lSmI];
	                else
	                    lBolBreak = true;
	        
	        lChAp1 = lChAp1[1, 2];
	    end
	    
	    lBolBreak = false;
	    
	    if lFnEstaVacio(lChAp2) then
	        lChAp2 = "  ";
	    else begin
	        if lChAp2.Length < 2 then
	            lChAp2 += " ".StrRepeat(2 - lChAp2.Length);
	        else
	            while (lSmI = lChAp2.Locate(" ")) > 0 and not lBolBreak do
	                if lChAp2[1, lSmI - 1] in ("DE", "LA", "DEL", "Y") then
	                    lChAp2 = lChAp2[lSmI + 1, lChAp2.Length - lSmI];
	                else
	                    lBolBreak = true;
	        
	        lChAp2 = lChAp2[1, 2];
	    end
	    
	    if lFnEstaVacio(lChNom) then
	        lChNom = "  ";
	    else
	        lChNom = lChNom[1];
	    
	    TxtAliasTC2 = lChAp1 + lChAp2 + lChNom;   
	end*/
		
		
		
		
	}
	
	/**
	 * genera un cdg siguiendo al maximo 
	 */
	public void generateCdg(){
		((Persona)getTo()).setCdg(Integer.parseInt(Utils.maxCode("Persona", "cdg"))+1);
	}
	
	/**
	 * Verifica los valores por defecto de los campos dejados a nulo
	 */
	public void verifyNullFields(){
		if(StringUtils.isEmpty(((Persona)getTo()).getTipovia().getCdg()))
			((Persona)getTo()).getTipovia().setCdg("CL");
		if(StringUtils.isEmpty(((Persona)getTo()).getTipdoc().getCdg()))
			((Persona)getTo()).setTipdoc(null);
		if(StringUtils.isEmpty(((Persona)getTo()).getPais().getCdg()))
			((Persona)getTo()).setPais(null);
		if(StringUtils.isEmpty(((Persona)getTo()).getPais1().getCdg()))
			((Persona)getTo()).setPais1(null);
		if(StringUtils.isEmpty(((Persona)getTo()).getProvincia().getCdg()))
			((Persona)getTo()).setProvincia(null);
		if(StringUtils.isEmpty(((Persona)getTo()).getProvincia1().getCdg()))
			((Persona)getTo()).setProvincia1(null);
		if(StringUtils.isEmpty(((Persona)getTo()).getNacion().getCdg()))
			((Persona)getTo()).setNacion(null);
	}
	
	private String tabName;

	/**
	 * Devuelve el tab seleccionado.
	 * El objetvo es mantener la pestaña activa entre navegaciones.
	 * 
	 * @return
	 */
	public String getTabName() {
		return tabName;
	}

	public void setTabName(String tabName) {
		this.tabName = tabName;
	}
	
	public void changeTabValue(ValueChangeEvent event){
		setTabName(event.getNewValue().toString());
	}

	public List<ITransferObject> getSelectedList() {
		return selectedList;
	}

	public void setSelectedList(List<ITransferObject> selectedList) {
		this.selectedList = selectedList;
	}

    
}


