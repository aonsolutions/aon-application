package com.code.aon.ui.payroll.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.payroll.cotizacion.CnaeMaestro;
import com.code.aon.payroll.cotizacion.Ocupacion;
import com.code.aon.payroll.dao.IPayrollAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;

public class CnaeMaestroControllerZahar extends PayrollBasicController implements IPayrollConstants {

	private List<SelectItem> listaOcupaciones;
	private IController ocupacionesController;
	private DataModel ocupacionesModel;
	private String ocupacion;

	/**
	 * Recupera las ocupaciones exclusivas a cnae
	 * 
	 * @return
	 */
	public List<SelectItem> getListaOcupaciones() {
		IController ocupacion = FormUtil.getController(OCUPACION_CONTROLLER_NAME);
		Criteria criteria = new Criteria();
		List<ITransferObject> ocupacionesCnae=null;
		
		try {
			criteria.addEqualExpression(getFieldName(IPayrollAlias.OCUPACION_OCUPACION_MAESTRO_EXCLUSIVO), "S");
			ocupacionesCnae = ocupacion.getManagerBean().getList(criteria);
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
				SelectItem item = new SelectItem( oc, name );
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

	
	
	
	
	public DataModel getOcupacionesModel() {
		return ocupacionesModel;
	}

	public void setOcupacionesModel(DataModel ocupacionesModel) {
		this.ocupacionesModel = ocupacionesModel;
	}

	public IController getOcupacionesController() {
		return ocupacionesController;
	}

	public void setOcupacionesController(IController ocupacionesController) {
		this.ocupacionesController = ocupacionesController;
	}
	
	public void initializeOcupacionesModel(){
		//CnaeMaestroController cnaeMaestro = (CnaeMaestroController) event.getController();
		CnaeMaestro cm = (CnaeMaestro)getTo();
		String ocupaciones = cm.getOcupacion();
		
		try {
			Criteria criteria = new Criteria();
			
			IManagerBean ocupacionBean = BeanManager.getManagerBean( Ocupacion.class );
			IController ocupacion = FormUtil.getController(OCUPACION_CONTROLLER_NAME);
			
			IManagerBean bean = BeanManager.getManagerBean( Ocupacion.class );
			List<Ocupacion> o = new ArrayList<Ocupacion>();
			DataModel dm;
			
			if(ocupaciones!=null){
				for (int i = 0; i < ocupaciones.length(); i++) {
					criteria.addOrExpression(ocupacionBean.getFieldName(IPayrollAlias.OCUPACION_OCUPACION_MAESTRO_CDG), ocupaciones.substring(i, i + 1));
					 List l = bean.getList( criteria );
					 if ( l != null ) {
						 o.add( (Ocupacion)l.iterator().next() );
					 }
					 criteria = new Criteria();
				}
				
				criteria.addEqualExpression(ocupacionBean.getFieldName(IPayrollAlias.OCUPACION_OCUPACION_MAESTRO_EXCLUSIVO), "S");
			} else {
				criteria.addEqualExpression(ocupacionBean.getFieldName(IPayrollAlias.OCUPACION_ID_CDG), "");
			}
			
			dm = new ListDataModel(o);
			setOcupacionesModel(dm);
			
			
			//ocupacion.setCriteria(criteria);
			//ocupacion.onSearch(null);
			
		} catch (ManagerBeanException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void initializeOcupacionesController(){
		initializeOcupacionesModel();
		
		ocupacionesController = new BasicController();
		
		//ocupacionesController.
		ocupacionesController.setModel(ocupacionesModel);
	}
	
	public void onOcupacionRemove(ValueChangeEvent event) {
		ocupacion = (String)event.getOldValue();
		String ocupacionesCnae =((CnaeMaestro)getTo()).getOcupacion();
		System.out.println("ocupacion: " + ocupacion);
		System.out.println("ocupacionesCnae: " + ocupacionesCnae);
		if(ocupacionesCnae.contains(ocupacion))
			System.out.println("Ocupacion ya asignada");
		else {
			//ocupacionesCnae.
			
		}
		
	}
	
	public void onOcupacionAdd(ValueChangeEvent event) {
		ocupacion = (String)event.getOldValue();
		((CnaeMaestro)getTo()).setOcupacion(((CnaeMaestro)getTo()).getOcupacion()+ocupacion);
		
	}
	
	
	
	/*
	 * ***********************************************
	 * ***********************************************
	 *     ^|^| ZAHARRA  ^|^|
	 * ***********************************************
	 * ***********************************************
	 */
	
	
	
	
	private List<SelectItem> listaOcuLibre;
	private List<SelectItem> listaOcuAsig;
	
	
	public List<SelectItem> getListaOcupacionesLibres() {
		IController ocupacion = FormUtil.getController(OCUPACION_CONTROLLER_NAME);
		Criteria criteria = new Criteria();
		List<ITransferObject> ocupacionesCnae=null;
		
		try {
			criteria.addEqualExpression(getFieldName(IPayrollAlias.OCUPACION_OCUPACION_MAESTRO_EXCLUSIVO), "S");
			ocupacionesCnae = ocupacion.getManagerBean().getList(criteria);
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("sin lista de ocupaciones cnae");
		}
		
		if(listaOcuLibre==null){
			//Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			listaOcuLibre = new LinkedList<SelectItem>();
			for (ITransferObject oc : ocupacionesCnae) {
				String name = ((Ocupacion)oc).getId().getCdg();
				SelectItem item = new SelectItem( oc, name );
				listaOcuLibre.add(item);
			}
		}
		return listaOcuLibre;
	}
	
	public List<SelectItem> getListaOcupacionesAsignadas() {
		
		return listaOcuAsig;
	}
	
	
	

}
