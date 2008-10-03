package com.code.aon.ui.customer.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.registry.Registry;

public class CustomerConverter implements Converter {

	boolean validate;
	
	@Override
	public Object getAsObject(FacesContext ctx, UIComponent comp, String value) {
		if (value == null || "".equals(value)
			) {
			return null;
		}
		Integer id;
		try {
			id = new Integer(value);	
		} catch (NumberFormatException e) {
			throw new ConverterException("El código de cliente '" + value +"' no es un número válido.");
		}
		
		try {
			IManagerBean  bean = BeanManager.getManagerBean(Customer.class);
			Customer c = (Customer) bean.get(id);
			if (validate && c==null) {
				throw new ConverterException("No existe un cliente para el código '" + value +"'");	
			}
			if (c==null) {
				c = new Customer();
				c.setId(id);
				Registry r = new Registry();
				c.setRegistry(r);
			}
			return c;
		} catch (ManagerBeanException e) {
			throw new ConverterException("No se puedo obtener el Manager de Customer.");
		}
	}

	@Override
	public String getAsString(FacesContext ctx, UIComponent c, Object value) {
		if (value == null) {
			return null;
		}
		if (value instanceof Customer) {
			Customer a = (Customer) value;
			if (a.getId() == null) {
				return null;
			}
			return a.getId().toString();
		}
		throw new ConverterException("Error de conversión en el cliente, "
				+ value.getClass().getName() + " no se puede convertir en Customer!.");
	}

	public boolean isValidate() {
		return validate;
	}

	public void setValidate(boolean validate) {
		this.validate = validate;
	}

}
