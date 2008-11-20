package com.code.aon.ui.common.converter;

import java.io.Serializable;

import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.SerializationUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;

/**
 * 
 *
 */
public class TransferObjectConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		ValueExpression vb = component.getValueExpression("value");
		Class<?> toType = (vb != null) ? vb.getType(context.getELContext()) : null;
		if (toType != null) {
			if (value != null) {
				try {
					IManagerBean bean = BeanManager.getManagerBean(toType);
			        byte[] data = Base64.decodeBase64(value.getBytes());
			        Serializable id = (Serializable) SerializationUtils.deserialize(data);
					ITransferObject to = bean.get(id);
					return to;
				} catch (ManagerBeanException e) {
					throw new ConverterException("Unable to find "
							+ ClassUtils.getShortClassName(toType) + " with id " + value);
				} catch (SecurityException e) {
					throw new ConverterException("Unable to find "
							+ ClassUtils.getShortClassName(toType) + " with id " + value);
				}
			}
			return null;
		}
		throw new ConverterException("Unable to find selectItems with TransferObject values.");
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value == null) {
			return null;
		}
		String string = null;
		try {
			Class<?> toType = value.getClass();
			IManagerBean bean = BeanManager.getManagerBean(toType);
			Serializable id = bean.getId( (ITransferObject) value );
			if ( id != null ) {
				byte[] data = SerializationUtils.serialize(id);
				string = new String( Base64.encodeBase64(data) );
			}
		} catch (Throwable th) {
			throw new ConverterException("Unable to get id from " + value);
		}
		return string;		
	}

}