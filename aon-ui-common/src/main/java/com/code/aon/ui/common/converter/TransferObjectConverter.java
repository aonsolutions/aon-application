package com.code.aon.ui.common.converter;

import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.ClassUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;

/**
 * 
 *
 */
public class TransferObjectConverter implements Converter {

	/* (non-Javadoc)
	 * @see javax.faces.convert.Converter#getAsObject(javax.faces.context.FacesContext, javax.faces.component.UIComponent, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		ValueExpression vb = component.getValueExpression("value");
		Class toType = vb == null ? null : vb.getType(context.getELContext());
		if (toType != null) {
			if (value != null) {
				try {
					IManagerBean bean = BeanManager.getManagerBean(toType);
					ITransferObject to = bean.get(Integer.valueOf(value));
					return to;
				} catch (ManagerBeanException e) {
					throw new ConverterException("Unable to find "
							+ ClassUtils.getShortClassName(toType) + " with id " + value);
				}
			}
			return null;
		}
		throw new ConverterException("Unable to find selectItems with TransferObject values.");
	}

	/* (non-Javadoc)
	 * @see javax.faces.convert.Converter#getAsString(javax.faces.context.FacesContext, javax.faces.component.UIComponent, java.lang.Object)
	 */
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value == null) {
			return null;
		}
		String id = null;
		try {
			id = BeanUtils.getProperty(value, "id");
		} catch (Throwable th) {
			throw new ConverterException("Unable to get id from " + value);
		}
		return id;
	}

}