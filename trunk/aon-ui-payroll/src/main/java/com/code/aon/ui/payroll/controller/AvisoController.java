package com.code.aon.ui.payroll.controller;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.validator.ValidatorException;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.payroll.dao.IPayrollAlias;
import com.code.aon.payroll.divisa.LinDivisa;
import com.code.aon.payroll.geograficas.Provincia;
import com.code.aon.payroll.principales.Cliente;
import com.code.aon.payroll.principales.Domicilio;
import com.code.aon.payroll.tipos.Tipovia;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;

public class AvisoController extends LinesController {

	

	Integer code ;
    public Integer getCode() throws ManagerBeanException {	
    	
    	
	       	code = 0;		
			String consulta = "select max(cdg) from Avisos";			
			Query q = HibernateUtil.getSession().createQuery(consulta);			
			List results = q.list();
			System.out.println("Max Code: " + results.get(0));   
			code= (Integer)results.get(0) +1;
			System.out.println("New Code: " + code);
			return code;		      
	     				
		}
    
    
	public void setCode(Integer code) {
		this.code = code;
	}
	private Cliente cliente;
	private Date fecha;
	
	
	@Override
	   public void onEditSearch(ActionEvent arg0) {
		   super.onEditSearch(arg0);
		   setCliente( new Cliente() );


	}
		
		
		@Override
		public void onSearch(ActionEvent event) {
		
		
			try {
				if  (cliente.getCdg() != null)  {
					
					getCriteria().addEqualExpression(getFieldName(IPayrollAlias.AVISOS_CLIENTE_CDG), getCliente().getCdg());
					
				}
				 if    (fecha != null){
						getCriteria().addEqualExpression(getFieldName(IPayrollAlias.AVISOS_FECHA), getFecha());
					}
			    
				
			} catch (ManagerBeanException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		
			
			super.onSearch(event);
		}



		public Cliente getCliente() {
			return cliente;
		}
		public void setCliente(Cliente cliente) {
			this.cliente = cliente;
		}


		public Date getFecha() {
			return fecha;
		}


		public void setFecha(Date fecha) {
			this.fecha = fecha;
		}

}


