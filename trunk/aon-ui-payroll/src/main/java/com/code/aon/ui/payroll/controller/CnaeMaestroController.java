package com.code.aon.ui.payroll.controller;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.SelectItem;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.payroll.cotizacion.CnaeMaestro;
import com.code.aon.payroll.cotizacion.Ocupacion;
import com.code.aon.payroll.dao.IPayrollAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.util.AonUtil;

public class CnaeMaestroController extends PayrollBasicController implements IPayrollConstants {

	private List<SelectItem> listaOcupaciones;
	private DataModel ocupaciones;

	/**
	 * Recupera las ocupaciones exclusivas a cnae
	 * 
	 * @return
	 */
	public List<SelectItem> getListaOcupaciones() {
		IController ocupacion = AonUtil.getController(OCUPACION_CONTROLLER_NAME);
		Criteria criteria = new Criteria();
		List<ITransferObject> ocupacionesCnae=null;
		
		try {
			//criteria.addEqualExpression(getFieldName(IPayrollAlias.OCUPACION_OCUPACION_MAESTRO_EXCLUSIVO), "S");
			ocupacionesCnae = ocupacion.getManagerBean().getList(null);
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("sin lista de ocupaciones cnae");
		}
		
		if(listaOcupaciones==null){
			//Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			listaOcupaciones = new LinkedList<SelectItem>();
			for (ITransferObject oc : ocupacionesCnae) {
				String name = ((Ocupacion)oc).getId().getCdg();
				SelectItem item = new SelectItem( name, name );
				listaOcupaciones.add(item);
			}
		}
		return listaOcupaciones;
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

	//añade la fecha al criteria para realizar busquedas
	@Override
	public void onSearch(ActionEvent event) {
		try {
			if(searchFecini != null){
				getCriteria().addEqualExpression(getFieldName(IPayrollAlias.LINBASEC_ID_FECINI), searchFecini);
			}
			if(searchFecfin != null){
				//getCriteria().addEqualExpression(getFieldName(event.getComponent().getId()), searchFecfin);
				getCriteria().addEqualExpression(getFieldName(IPayrollAlias.LINBASEC_FECFIN), searchFecfin);
			}
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		searchFecini=null;
		searchFecfin=null;
		
		super.onSearch(event);
	}

	public DataModel getOcupaciones() {
		return ocupaciones;
	}

	public void setOcupaciones(DataModel ocupaciones) {
		this.ocupaciones = ocupaciones;
	}

}
