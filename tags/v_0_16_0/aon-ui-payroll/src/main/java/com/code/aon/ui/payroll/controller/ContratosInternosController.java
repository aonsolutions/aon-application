package com.code.aon.ui.payroll.controller;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.payroll.cotizacion.PorcentajeMaestro;
import com.code.aon.payroll.dao.IPayrollAlias;
import com.code.aon.payroll.enumeration.Desempleado;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionUtilities;

public class ContratosInternosController extends PayrollBasicController {

	private List<SelectItem> asimilados;
	private List<SelectItem> desempleados;

	/**
	 * Recupera los Asimilados asociados al % de cotizacion 'DESEMPL%'.
	 * 
	 * @return
	 */
	public List<SelectItem> getAsimilados() {
		return asimilados;
	}	

	/**
	 * Recupera el enumerado de Desempleados.
	 * 
	 * @return
	 */
	public List<SelectItem> getDesempleados() {
		if ( desempleados == null ) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			desempleados = new LinkedList<SelectItem>();
			for (Desempleado desempleado : Desempleado.values()) {
				String name = desempleado.getName( locale );
				SelectItem item = new SelectItem( desempleado, name );
				desempleados.add(item);
			}
		}
		return desempleados;
	}

	@SuppressWarnings("unchecked")
	public void refreshAsimilados() throws ManagerBeanException {
		asimilados = new LinkedList<SelectItem>();
		asimilados.add( new SelectItem( IPayrollConstants.EMPTY_STRING, IPayrollConstants.EMPTY_STRING ) );
		IManagerBean bean = BeanManager.getManagerBean(PorcentajeMaestro.class);
		Criteria criteria = new Criteria();
		String identifier = bean.getFieldName( IPayrollAlias.PORCENTAJE_MAESTRO_CDG );
		criteria.addExpression( ExpressionUtilities.getLikeExpression( identifier, "DESEMPL%" ) );
		Iterator<ITransferObject> iter = bean.getList(criteria).iterator();
		while(iter.hasNext()){
			PorcentajeMaestro pm = (PorcentajeMaestro) iter.next();
			SelectItem item = new SelectItem( pm.getCdg(), pm.getDescription() );
			asimilados.add(item);
		}
	}
	
	
	private boolean searchMujersub;
	private boolean searchIncaread;
	private boolean searchPrimertra;
	private boolean searchExcsocial;
	
	//añade el valor de los checkbox al criteria para realizar busquedas
	@Override
	public void onSearch(ActionEvent event) {
		
		try {
			if(searchMujersub)
				getCriteria().addEqualExpression(getFieldName(IPayrollAlias.CONTRATOS_INTERNOS_MUJERSUB), true);
			if(searchIncaread)
				getCriteria().addEqualExpression(getFieldName(IPayrollAlias.CONTRATOS_INTERNOS_INCAREAD), true);
			if(searchPrimertra)
				getCriteria().addEqualExpression(getFieldName(IPayrollAlias.CONTRATOS_INTERNOS_PRIMERTRA), true);
			if(searchExcsocial)
				getCriteria().addEqualExpression(getFieldName(IPayrollAlias.CONTRATOS_INTERNOS_EXCSOCIAL), true);	
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		searchMujersub=false;
		searchIncaread=false;
		searchPrimertra=false;
		searchExcsocial=false;
		
		super.onSearch(event);
	}

	public boolean getSearchMujersub() {
		return searchMujersub;
	}

	public void setSearchMujersub(boolean searchMujersub) {
		this.searchMujersub = searchMujersub;
	}

	public boolean getSearchIncaread() {
		return searchIncaread;
	}

	public void setSearchIncaread(boolean searchIncaread) {
		this.searchIncaread = searchIncaread;
	}

	public boolean getSearchPrimertra() {
		return searchPrimertra;
	}

	public void setSearchPrimertra(boolean searchPrimertra) {
		this.searchPrimertra = searchPrimertra;
	}

	public boolean getSearchExcsocial() {
		return searchExcsocial;
	}

	public void setSearchExcsocial(boolean searchExcsocial) {
		this.searchExcsocial = searchExcsocial;
	}

}
