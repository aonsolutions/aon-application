package com.code.aon.ui.project.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.project.DossierType;

public class DossierTypeConverter implements Converter {

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
			throw new ConverterException("El tipo de dossier '" + value +"' no es un número válido.");
		}
		
		try {
			IManagerBean  bean = BeanManager.getManagerBean(DossierType.class);
			DossierType c = (DossierType) bean.get(id);
			if (validate && c==null) {
				throw new ConverterException("No existe un tipo de dossier para el código '" + value +"'");	
			}
			return c;
		} catch (ManagerBeanException e) {
			throw new ConverterException("No se puedo obtener el Manager de DossierType.");
		}
	}

	@Override
	public String getAsString(FacesContext ctx, UIComponent c, Object value) {
		if (value == null) {
			return null;
		}
		if (value instanceof DossierType) {
			DossierType a = (DossierType) value;
			if (a.getId() == null) {
				return null;
			}
			return a.getId().toString();
		}
		throw new ConverterException("Error de conversión en el tipo de dossier, "
				+ value.getClass().getName() + " no se puede convertir en DossierType!.");
	}

	public boolean isValidate() {
		return validate;
	}

	public void setValidate(boolean validate) {
		this.validate = validate;
	}

}
