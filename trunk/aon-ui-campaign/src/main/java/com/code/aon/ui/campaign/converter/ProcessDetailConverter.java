package com.code.aon.ui.campaign.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import com.code.aon.campaign.ProcessDetail;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;

public class ProcessDetailConverter implements Converter {

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
			throw new ConverterException("El código de proceso '" + value +"' no es un número válido.");
		}
		
		try {
			IManagerBean  bean = BeanManager.getManagerBean(ProcessDetail.class);
			ProcessDetail p = (ProcessDetail) bean.get(id);
			if (validate && p==null) {
				throw new ConverterException("No existe un proceso para el código '" + value +"'");	
			}
			if (p==null) {
				p = new ProcessDetail();
				p.setId(id);
				p.setDescription(" -------------------------- ");
			}
			return p;
		} catch (ManagerBeanException e) {
			throw new ConverterException("No se puedo obtener el Manager de Process.");
		}
	}

	@Override
	public String getAsString(FacesContext ctx, UIComponent c, Object value) {
		if (value == null) {
			return null;
		}
		if (value instanceof ProcessDetail) {
			ProcessDetail a = (ProcessDetail) value;
			if (a.getId() == null) {
				return null;
			}
			return a.getId().toString();
		}
		throw new ConverterException("Error de conversión en el proceso, "
				+ value.getClass().getName() + " no se puede convertir en Process!.");
	}

	public boolean isValidate() {
		return validate;
	}

	public void setValidate(boolean validate) {
		this.validate = validate;
	}

}
