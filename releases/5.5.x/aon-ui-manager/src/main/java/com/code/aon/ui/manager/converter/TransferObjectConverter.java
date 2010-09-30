package com.code.aon.ui.manager.converter;

import java.io.Serializable;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.math.NumberUtils;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.form.IController;

public class TransferObjectConverter implements Converter {

	private IController controller;
	
	public TransferObjectConverter(IController controller) {
		this.controller = controller;
	}
	
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (value != null) {
			try {
				Integer id = NumberUtils.toInt(value);
				ITransferObject to = controller.getManagerBean().get(id);
				return to;
			} catch (ManagerBeanException e) {
				throw new ConverterException("Unable to find ITransferObject with id " + value);
			}
		}
		return null;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value == null) {
			return null;
		}
		String string = null;
		try {
			Serializable id = controller.getManagerBean().getId( (ITransferObject) value );
			string = ObjectUtils.toString(id);
		} catch (Throwable th) {
			throw new ConverterException("Unable to get id from " + value);
		}
		return string;		
	}

}