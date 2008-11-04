package com.code.aon.ui.payroll.controller;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.SelectItem;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.payroll.dao.IPayrollAlias;
import com.code.aon.payroll.enumeration.TipoComplemento;
import com.code.aon.payroll.enumeration.TipoCotizaciones;
import com.code.aon.payroll.enumeration.Retribuciones;
import com.code.aon.payroll.enumeration.FijoVariable;
import com.code.aon.payroll.enumeration.IndiceComplemento;
import com.code.aon.payroll.enumeration.Prorateo;
import com.code.aon.ui.form.BasicController;

public class BasesController extends PayrollBasicController {

	
	private List<SelectItem> prorateos;


	
	
	/**
	 * Recupera los tipos de retribuciones 
	 * 
	 * @return
	 */
	public List<SelectItem> getListaProrateos() {
		if(prorateos==null){
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			prorateos = new LinkedList<SelectItem>();
			for (Prorateo p : Prorateo.values()) {
				String name = p.getName( locale );
				SelectItem item = new SelectItem( p, name );
				prorateos.add(item);
			}
		}
		return prorateos;
	}

	
	private Date searchFecini;
	private Date searchFecfin;
	
	public Date getSearchFecini() {
		return searchFecini;
	}

	public void setSearchFecini(Date searchFecini) {
		this.searchFecini = searchFecini;
	}

	public Date getSearchFecfin() {
		return searchFecfin;
	}

	public void setSearchFecfin(Date searchFecfin) {
		this.searchFecfin = searchFecfin;
	}

	
	@Override
	public void onSearch(ActionEvent event) {
		System.out.println("------------"+searchFecini);
		System.out.println("------------"+searchFecfin);
		try {
			if(searchFecini != null){
				getCriteria().addEqualExpression(getFieldName(IPayrollAlias.BASE_LINBASES_ID_FECINI), searchFecini);
			}
			if(searchFecfin != null){
			
				getCriteria().addEqualExpression(getFieldName(IPayrollAlias.BASE_LINBASES_FECFIN), searchFecfin);
			}
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		searchFecini=null;
		searchFecfin=null;
		
		super.onSearch(event);
	}
	






}
