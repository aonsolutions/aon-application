package com.code.aon.ui.campaign.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import com.code.aon.campaign.ProcessTransitionType;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;

public class ProcessTransitionTypeConverter implements Converter {

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
			throw new ConverterException("El código de tipo de transición '" + value +"' no es un número válido.");
		}
		
		try {
			IManagerBean  bean = BeanManager.getManagerBean(ProcessTransitionType.class);
			ProcessTransitionType p = (ProcessTransitionType) bean.get(id);
			if (validate && p==null) {
				throw new ConverterException("No existe un tipo de transición para el código '" + value +"'");	
			}
			if (p==null) {
				p = new ProcessTransitionType();
				p.setId(id);
				p.setDescription(" -------------------------- ");
			}
			return p;
		} catch (ManagerBeanException e) {
			throw new ConverterException("No se puedo obtener el Manager de Tipos de transiciones.");
		}
	}

	@Override
	public String getAsString(FacesContext ctx, UIComponent c, Object value) {
		if (value == null) {
			return null;
		}
		if (value instanceof ProcessTransitionType) {
			ProcessTransitionType a = (ProcessTransitionType) value;
			if (a.getId() == null) {
				return null;
			}
			return a.getId().toString();
		}
		throw new ConverterException("Error de conversión en el tipo de transicion, "
				+ value.getClass().getName() + " no se puede convertir en ProcessTransitionType!.");
	}

	public boolean isValidate() {
		return validate;
	}

	public void setValidate(boolean validate) {
		this.validate = validate;
	}

}
