package com.code.aon.ui.payroll.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.payroll.cotizacion.CnaeMaestro;
import com.code.aon.payroll.cotizacion.Ocupacion;
import com.code.aon.payroll.dao.IPayrollAlias;
import com.code.aon.ql.Criteria;

public class CnaeMaestroController extends PayrollBasicController implements IPayrollConstants {

	private static final Logger LOGGER = Logger.getLogger(CotizacionBonificacionController.class.getName());
	
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

	/**
	 * añade la fecha al criteria para realizar busquedas
	 * y llama al metodo onSearch de la superclase
	 */
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
			LOGGER.log( Level.SEVERE, e.getMessage(), e );
			//e.printStackTrace();
		}
		searchFecini=null;
		searchFecfin=null;
		
		super.onSearch(event);
	}

	
	/*
	 * ***********************************************
	 * ***********************************************
	 *     ^|^| ZAHARRA  ^|^|
	 * ***********************************************
	 * ***********************************************
	 */
	
	private List<ITransferObject> listaOcupaciones;
	/**
	 * inicializa la lista de ocupaciones exclusivas a CNAE.
	 * se obtiene unicamente la primera ocurrencia por cada codigo de ocupacion.
	 */
	public void initializeOcupacionesList(){
		
		if(listaOcupaciones==null){
			try {
				IManagerBean ocupacionBean = BeanManager.getManagerBean( Ocupacion.class );
				
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(ocupacionBean.getFieldName(IPayrollAlias.OCUPACION_OCUPACION_MAESTRO_EXCLUSIVO), "S");
				
				List<ITransferObject> ocupacionesBean;
				ocupacionesBean = ocupacionBean.getList(criteria);
				listaOcupaciones = new LinkedList<ITransferObject>();
				
				List<String> listaCdg = new ArrayList<String>();
				
				for (ITransferObject o : ocupacionesBean) {
					if(!listaCdg.contains(((Ocupacion)o).getId().getCdg())) {
						listaOcupaciones.add(o);
						listaCdg.add(((Ocupacion)o).getId().getCdg());
					}
				}
			} catch (ManagerBeanException e) {
				LOGGER.log( Level.SEVERE, e.getMessage(), e );
				e.printStackTrace();
			}
		}
	}
	
	
	
	private List<ITransferObject> listaOcuLibre;
	private List<ITransferObject> listaOcuAsig;
	/**
	 * inicializa las listas con las ocupaciones asignadas en una y las libres en otra
	 */
	public void initializeAsignedLists(){
		CnaeMaestro cm = (CnaeMaestro)getTo();
		String ocupaciones = " ";
		ocupaciones = cm.getOcupacion();
		
		//ocupaciones.substring(i, i + 1)
		
		listaOcuLibre = new LinkedList<ITransferObject> ();
		listaOcuAsig = new LinkedList<ITransferObject> ();
		
		if(ocupaciones!=null){
			for (ITransferObject o : listaOcupaciones) {
		
				if(ocupaciones.contains(((Ocupacion)o).getId().getCdg())) {
					String name = ((Ocupacion)o).getId().getCdg();
					SelectItem item = new SelectItem( ((Ocupacion)o), name );
					listaOcuAsig.add(o);
				} else {
					String name = ((Ocupacion)o).getId().getCdg();
					SelectItem item = new SelectItem( ((Ocupacion)o), name );
					listaOcuLibre.add(o);
				}
			
			}
		} else {
			for (ITransferObject o : listaOcupaciones) {
				String name = ((Ocupacion)o).getId().getCdg();
				SelectItem item = new SelectItem( ((Ocupacion)o), name );
				listaOcuLibre.add(o);
			}
			
		}
			

			
			
		
		
		
		
		
		
		/*
		CnaeMaestro cm = (CnaeMaestro)getTo();
		String ocupaciones = cm.getOcupacion();
		
		try {
			IManagerBean ocupacionBean = BeanManager.getManagerBean( Ocupacion.class );
			//IController ocupacion = AonUtil.getController(OCUPACION_CONTROLLER_NAME);
			
			
			IManagerBean beanOcupacion = BeanManager.getManagerBean( Ocupacion.class );
			List<Ocupacion> listaOcupaciones = new ArrayList<Ocupacion>();
			//DataModel dm;
			
			Criteria criteria = new Criteria();
			
			if(ocupaciones!=null){
				criteria.addEqualExpression(ocupacionBean.getFieldName(IPayrollAlias.OCUPACION_OCUPACION_MAESTRO_EXCLUSIVO), "S");
				List<ITransferObject> toList = beanOcupacion.getList( criteria );
				
				for (int i = 0; i < ocupaciones.length(); i++) {
					//criteria.addOrExpression(ocupacionBean.getFieldName(IPayrollAlias.OCUPACION_OCUPACION_MAESTRO_CDG), ocupaciones.substring(i, i + 1));
					
					
					
					if ( toList != null ) {
						listaOcupaciones.add( (Ocupacion)toList.iterator().next() );
					}
					
					
					if(listaOcuAsig==null){
						//Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
						listaOcuAsig = new LinkedList<SelectItem>();
						for (ITransferObject oc : listaOcupaciones) {
							String name = ((Ocupacion)oc).getId().getCdg();
							SelectItem item = new SelectItem( oc, name );
							listaOcuAsig.add(item);
						}
					}
					
					
					
				}	
				
			} else {
				listaOcuAsig=null;
				listaOcuLibre=null;
				criteria.addEqualExpression(ocupacionBean.getFieldName(IPayrollAlias.OCUPACION_ID_CDG), "");
			}
			
			
		} catch (ManagerBeanException e1) {
			LOGGER.log( Level.SEVERE, e1.getMessage(), e1 );
			//e1.printStackTrace();
		} */
	}
	
	
	
	public List<ITransferObject> getListaOcupacionesLibres() {
		
		return listaOcuLibre;
	}
	
	public List<ITransferObject> getListaOcupacionesAsignadas() {
		
		return listaOcuAsig;
	}
	
	public List<ITransferObject> getListaOcupaciones() {
		
		return listaOcupaciones;
	}
	
	public void refreshOcupaciones(ValueChangeEvent event){
		((CnaeMaestro)getTo()).setOcupacion(((Ocupacion)listaOcuAsig).getId().getCdg());
	}
	
	
	

}
