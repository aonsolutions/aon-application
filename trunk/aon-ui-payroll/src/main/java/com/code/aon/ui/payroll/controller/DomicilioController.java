package com.code.aon.ui.payroll.controller;

import java.util.List;

import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.payroll.dao.IPayrollAlias;
import com.code.aon.payroll.geograficas.Provincia;
import com.code.aon.payroll.principales.Cliente;
import com.code.aon.payroll.tipos.Tipovia;
import com.code.aon.ui.form.LinesController;

public class DomicilioController extends LinesController {

	
	
private Cliente cliente;
private Tipovia tipovia;
private Provincia provincia;
	@Override
   public void onEditSearch(ActionEvent arg0) {
	   super.onEditSearch(arg0);
	   setCliente( new Cliente() );
       setTipovia( new Tipovia() );
	   setProvincia( new Provincia() );
      
       
}
	
	@Override
	public void onSearch(ActionEvent event) {
		
		
		
		try {
			if  (cliente.getCdg() != null)  {
				getCriteria().addEqualExpression(getFieldName(IPayrollAlias.DOMICILIO_CLIENTE_CDG), getCliente().getCdg());
			}
			
		    if  ( (tipovia.getCdg() != null) && (! StringUtils.isEmpty(tipovia.getCdg())) ) {
				getCriteria().addEqualExpression(getFieldName(IPayrollAlias.DOMICILIO_TIPOVIA_CDG), getTipovia().getCdg());
			}
			
		     if  ( (provincia.getCdg() != null) && (! StringUtils.isEmpty(provincia.getCdg())) ) {
				getCriteria().addEqualExpression(getFieldName(IPayrollAlias.DOMICILIO_PROVINCIA_CDG), getProvincia().getCdg());
			}
			
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	
		
		super.onSearch(event);
	}
	
	Integer code ;
    public Integer getCode() throws ManagerBeanException {	
    	
    	
	       	code = 0;		
			String consulta = "select max(cdg) from Domicilio";			
			Query q = HibernateUtil.getSession().createQuery(consulta);			
			List results = q.list();
			System.out.println("Max Code: " + results.get(0));   
			code= (Integer)results.get(0) +1;
			System.out.println("New Code: " + code);
			return code;		      
	     				
		}

	public Cliente getCliente() {
		return cliente;
	}
	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
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

	public void setCode(Integer code) {
		this.code = code;
	}




}


