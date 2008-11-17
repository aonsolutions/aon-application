package com.code.aon.ui.payroll.controller;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.validator.ValidatorException;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.payroll.dao.IPayrollAlias;
import com.code.aon.payroll.geograficas.Provincia;
import com.code.aon.payroll.principales.Cliente;
import com.code.aon.payroll.principales.Domicilio;
import com.code.aon.payroll.tipos.Tipovia;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;

public class CuentaController extends LinesController {

	
	
private Cliente cliente;

	@Override
   public void onEditSearch(ActionEvent arg0) {
	   super.onEditSearch(arg0);
	   setCliente( new Cliente() );


}
	
	@Override
	public void onSearch(ActionEvent event) {
		System.out.println("----------c"+cliente.getCdg());
	
		try {
			if  (cliente.getCdg() != null)  {
				
				getCriteria().addEqualExpression(getFieldName(IPayrollAlias.CUENTAS_CLIENTE_CDG), getCliente().getCdg());
				System.out.println("----------c"+cliente.getCdg());
			}
		
			
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		System.out.println("----------c"+cliente.getCdg());
		
		super.onSearch(event);
	}



	public Cliente getCliente() {
		return cliente;
	}
	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

}


