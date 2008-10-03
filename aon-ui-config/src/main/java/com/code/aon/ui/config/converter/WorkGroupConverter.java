package com.code.aon.ui.config.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.WorkGroup;

public class WorkGroupConverter implements Converter {

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
			throw new ConverterException("El código de grupo '" + value +"' no es un número válido.");
		}
		
		try {
			IManagerBean  bean = BeanManager.getManagerBean(WorkGroup.class);
			WorkGroup p = (WorkGroup) bean.get(id);
			if (validate && p==null) {
				throw new ConverterException("No existe un grupo para el código '" + value +"'");	
			}
			if (p==null) {
				p = new WorkGroup();
				p.setId(id);
				p.setDescription(" -------------------------- ");
			}
			return p;
		} catch (ManagerBeanException e) {
			throw new ConverterException("No se puedo obtener el Manager de WorkGroup.");
		}
	}

	@Override
	public String getAsString(FacesContext ctx, UIComponent c, Object value) {
		if (value == null) {
			return null;
		}
		if (value instanceof WorkGroup) {
			WorkGroup a = (WorkGroup) value;
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
