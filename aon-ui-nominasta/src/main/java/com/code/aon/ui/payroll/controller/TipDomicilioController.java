package com.code.aon.ui.payroll.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

import org.hibernate.Query;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.payroll.dao.IPayrollAlias;
import com.code.aon.payroll.enumeration.Tipdom;
import com.code.aon.payroll.principales.Cliente;
import com.code.aon.payroll.principales.empresa.Actividad;
import com.code.aon.payroll.principales.empresa.Emprdom;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;


public class TipDomicilioController extends LinesController {


	private String tipo;
	List<ITransferObject> tiposdomicilio;
	DataModel modeldomicilios;
	
	
	private List<SelectItem> listatiposdomicilio;

	public List<SelectItem> getListaTipos() {
		if(listatiposdomicilio==null){
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			listatiposdomicilio = new LinkedList<SelectItem>();
			for (Tipdom e : Tipdom.values()) {
				String name = e.getName( locale );
				SelectItem item = new SelectItem( e, name );
				listatiposdomicilio.add(item);
			}
		}
		return listatiposdomicilio;
	}
	
	@Override
	public void onReset(ActionEvent event) {
		
		super.onReset(event);
		
		try {
			((Emprdom)(getTo())).setCdg(getCode());
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	

	Integer code ;
    public Integer getCode() throws ManagerBeanException {	
    	
    	
	       	code = 0;		
			String consulta = "select max(cdg) from Emprdom";			
			Query q = HibernateUtil.getSession().createQuery(consulta);			
			List results = q.list();
			System.out.println("Max Code: " + results.get(0));   
			code= (Integer)results.get(0) +1;
			System.out.println("New Code: " + code);
			return code;		      
	     				
		}
    
    
    public void getTypes() throws ManagerBeanException {	
    	
    		IManagerBean bean = BeanManager.getManagerBean(Emprdom.class);
     		Criteria criteria = new Criteria();    		
    		String alias = bean.getFieldName(IPayrollAlias.EMPRDOM_TIPDOM);
    		criteria.addEqualExpression(alias,tipo);
    		List<ITransferObject> list = bean.getList(criteria);
    		setTiposdomicilio(list);
        	modeldomicilios    = new ListDataModel( getTiposDomicilio() );
             
    		
	
	}
	


	public List<ITransferObject> getTiposDomicilio() {

		try {
			if (tiposdomicilio == null) {
				initialiceTiposDomicilio();
			}
			return tiposdomicilio;
		} catch (ManagerBeanException e) {
			String msg = "Error cargando tipos de domicilio";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}

	public void  initialiceTiposDomicilio() throws ManagerBeanException {

		Integer cdg = ((Cliente)(FormUtil.getController(IPayrollConstants.CLIENTE_CONTROLLER_NAME)).getTo()).getCdg();
		Integer cdg2 = ((Actividad)(FormUtil.getController(IPayrollConstants.ACTIVIDAD_CONTROLLER_NAME)).getTo()).getCdg();

		
		IManagerBean bean = BeanManager.getManagerBean(Emprdom.class);
 		Criteria criteria = new Criteria();   
 		
 		String alias = bean.getFieldName(IPayrollAlias.EMPRDOM_CLIENTE_CDG);
 		String alias2 = bean.getFieldName(IPayrollAlias.EMPRDOM_ACTIVIDAD_CDG);
        
 		criteria.addEqualExpression(alias, cdg);      
        criteria.addEqualExpression(alias2, cdg2);
        List<ITransferObject> list = bean.getList(criteria);
    	setTiposdomicilio(list);
    	modeldomicilios    = new ListDataModel( getTiposDomicilio() );
         
    	
    
	
	}


        public List<ITransferObject> getTiposdomicilio() {
               return tiposdomicilio;
        }


       public void setTiposdomicilio(List<ITransferObject> tiposdomicilio) {
	          this.tiposdomicilio = tiposdomicilio;
        }

@Override
	public void onAccept(ActionEvent event) {
		verifyNullFields();
		super.onAccept(event);
	}
	
	
	/**
	 * comprueba los nulos de los objetos complejos
	 * para ponerlos a null en el caso de que esten vacios
	 */
	private void verifyNullFields(){
		
		if(((Emprdom)getTo()).getActividad()!=null  && ((Emprdom)getTo()).getActividad().getCdg() == null)
			((Emprdom)getTo()).setActividad(null);
		
	}



	


	

}
