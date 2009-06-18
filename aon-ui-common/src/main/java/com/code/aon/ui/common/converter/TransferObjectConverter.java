package com.code.aon.ui.common.converter;

import java.lang.reflect.Field;

import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.ClassUtils;
import org.hibernate.metadata.ClassMetadata;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;

/**
 * 
 *
 */
public class TransferObjectConverter implements Converter {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.faces.convert.Converter#getAsObject(javax.faces.context.FacesContext
	 * , javax.faces.component.UIComponent, java.lang.String)
	 */
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		ValueExpression vb = component.getValueExpression("value");
		Class<?> toType = vb == null ? null : vb.getType(context.getELContext());
		if (toType != null) {
			if (value != null) {
				try {
					IManagerBean bean = BeanManager.getManagerBean(toType);
					ClassMetadata cm = HibernateUtil.getSessionFactory().getClassMetadata(toType);
					String id = cm.getIdentifierPropertyName();
					Field field = toType.getDeclaredField(id);
					Class<?> typeClass = field.getType();
					ITransferObject to = null;
					if (typeClass.equals(String.class)) {
						to = bean.get(value);
					} else if (typeClass.equals(Integer.class)) {
						to = bean.get(Integer.valueOf(value));
					} else {
						throw new ConverterException(
								"Unsupported PK type, types supported: " 
								+"java.lang.String and java.lang.Integer. Found "
								+ toType.getName());
					}
					return to;
				} catch (ManagerBeanException e) {
					throw new ConverterException("Unable to find "
							+ ClassUtils.getShortClassName(toType) + " with id " + value);
				} catch (SecurityException e) {
					throw new ConverterException("Unable to find "
							+ ClassUtils.getShortClassName(toType) + " with id " + value);
				} catch (NoSuchFieldException e) {
					throw new ConverterException("Unable to find "
							+ ClassUtils.getShortClassName(toType) + " with id " + value);
				}
			}
			return null;
		}
		throw new ConverterException("Unable to find selectItems with TransferObject values.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.faces.convert.Converter#getAsString(javax.faces.context.FacesContext
	 * , javax.faces.component.UIComponent, java.lang.Object)
	 */
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value == null) {
			return null;
		}
		String id = null;
		try {
			Class<?> type = value.getClass();
			ClassMetadata cm = HibernateUtil.getSessionFactory().getClassMetadata(type);
			String idName = cm.getIdentifierPropertyName();
			id = BeanUtils.getProperty(value, idName);
		} catch (Throwable th) {
			throw new ConverterException("Unable to get id from " + value);
		}
		return id;
	}

}