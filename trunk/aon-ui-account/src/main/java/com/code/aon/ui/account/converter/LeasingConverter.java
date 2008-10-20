package com.code.aon.ui.account.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import com.code.aon.account.Leasing;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;

public class LeasingConverter implements Converter {

	boolean validate;
	
	@Override
	public Object getAsObject(FacesContext ctx, UIComponent c, String value) {
		if (value == null) {
			return null;
		}
		Integer id;
		try {
			id = new Integer(value);	
		} catch (NumberFormatException e) {
			throw new ConverterException("El código de leasing '" + value +"' no es un número válido.");
		}
		try {
			IManagerBean  bean = BeanManager.getManagerBean(Leasing.class);
			Leasing a = (Leasing) bean.get(id);
			if (validate && a==null) {
				throw new ConverterException("No existe una cuenta contable para el código '" + value +"'");	
			}
			if (a==null) {
				a = new Leasing();
			}
			return a;
		} catch (ManagerBeanException e) {
			throw new ConverterException("No se puedo obtener el Manager de Account.");
		}
	}

	@Override
	public String getAsString(FacesContext ctx, UIComponent c, Object value) {
		if (value == null) {
			return null;
		}
		if (value instanceof Leasing) {
			Leasing a = (Leasing) value;
			return a.getId().toString();
		}
		throw new ConverterException("Error de conversión en la cuenta, "
				+ value.getClass().getName() + " no se puede convertir en Account!.");
	}

	public boolean isValidate() {
		return validate;
	}

	public void setValidate(boolean validate) {
		this.validate = validate;
	}

}
