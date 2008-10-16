package com.code.aon.ui.supplier.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.registry.Registry;
import com.code.aon.supplier.Supplier;

public class SupplierConverter implements Converter {

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
			throw new ConverterException("El código de proveedor '" + value +"' no es un número válido.");
		}
		
		try {
			IManagerBean  bean = BeanManager.getManagerBean(Supplier.class);
			Supplier c = (Supplier) bean.get(value);
			if (validate && c==null) {
				throw new ConverterException("No existe un proveedor para el código '" + value +"'");	
			}
			if (c==null) {
				c = new Supplier();
				c.setId(id);
				Registry r = new Registry();
				r.setName(" ¡Proveedor no válido! ");
				c.setRegistry(r);
			}
			return c;
		} catch (ManagerBeanException e) {
			throw new ConverterException("No se puedo obtener el Manager de Supplier.");
		}
	}

	@Override
	public String getAsString(FacesContext ctx, UIComponent c, Object value) {
		if (value == null) {
			return null;
		}
		if (value instanceof Supplier) {
			Supplier a = (Supplier) value;
			return a.getId().toString();
		}
		throw new ConverterException("Error de conversión en el proveedor, "
				+ value.getClass().getName() + " no se puede convertir en Supplier!.");
	}

	public boolean isValidate() {
		return validate;
	}

	public void setValidate(boolean validate) {
		this.validate = validate;
	}

}
