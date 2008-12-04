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
import org.hibernate.Query;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.payroll.cotizacion.Linepigr;
import com.code.aon.payroll.dao.IPayrollAlias;
import com.code.aon.payroll.enumeration.Epigrafes;
import com.code.aon.payroll.enumeration.Tipcuenta;
import com.code.aon.payroll.enumeration.Tipdom;
import com.code.aon.payroll.principales.empresa.Emprdom;
import com.code.aon.payroll.principales.empresa.Empresa;
import com.code.aon.ui.form.LinesController;

public class TipDomicilioController extends LinesController {


	
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
