package com.code.aon.ui.finance.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.registry.Registry;
import com.code.aon.finance.Creditor;

public class CreditorConverter implements Converter {

	boolean validate;
	
	@Override
	public Object getAsObject(FacesContext ctx, UIComponent comp, String value) {
		if (value == null) {
			return null;
		}
		Integer id;
		try {
			id = new Integer(value);	
		} catch (NumberFormatException e) {
			throw new ConverterException("El código de acreedor '" + value +"' no es un número válido.");
		}
		
		try {
			IManagerBean  bean = BeanManager.getManagerBean(Creditor.class);
			Creditor c = (Creditor) bean.get(value);
			if (validate && c==null) {
				throw new ConverterException("No existe un acreedor para el código '" + value +"'");	
			}
			if (c==null) {
				c = new Creditor();
				c.setId(id);
				Registry r = new Registry();
				c.setRegistry(r);
			}
			return c;
		} catch (ManagerBeanException e) {
			throw new ConverterException("No se puedo obtener el Manager de Creditor.");
		}
	}

	@Override
	public String getAsString(FacesContext ctx, UIComponent c, Object value) {
		if (value == null) {
			return null;
		}
		if (value instanceof Creditor) {
			Creditor a = (Creditor) value;
			return a.getId().toString();
		}
		throw new ConverterException("Error de conversión en el acreedor, "
				+ value.getClass().getName() + " no se puede convertir en Creditor!.");
	}

	public boolean isValidate() {
		return validate;
	}

	public void setValidate(boolean validate) {
		this.validate = validate;
	}

}
