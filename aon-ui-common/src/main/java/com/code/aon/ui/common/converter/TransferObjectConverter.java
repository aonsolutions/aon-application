package com.code.aon.ui.common.converter;

import java.io.Serializable;

import jakarta.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;

/**
 * 
 *
 */
public class TransferObjectConverter implements Converter {

	private final static Logger LOGGER = LoggerFactory.getLogger(TransferObjectConverter.class);
	
	private Class<?> type;
	
	public Class<?> getType() {
		return type;
	}

	public void setType(Class<?> type) {
		this.type = type;
	}

	private Serializable getId( String value ) {
		Serializable id = null;
		try {
	        byte[] data = Base64.decodeBase64(value.getBytes());
	        id = (Serializable) SerializationUtils.deserialize(data);			
		} catch ( Throwable th ) {
			LOGGER.debug( "Error deserializing: " + value, th );	
		}
        return id;
	}
	
	private Class<?> getType( FacesContext context, UIComponent component ) {
		if ( type == null ) {
			ValueExpression vb = component.getValueExpression("value");
			Class<?> toType = (vb != null) ? vb.getType(context.getELContext()) : null;
			if ( (toType != null) && toType.isArray() ) {
				return toType.getComponentType();
			}
			return toType;			
		}
		return type;
	}
	
	@Override
	@SuppressWarnings("unchecked")	
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		Class<?> toType = getType(context, component);
		if (toType != null) {
			if (value != null) {
				try {
					Serializable id = getId(value);
					if ( id != null ) {
						IManagerBean bean = BeanManager.getManagerBean( (Class<? extends ITransferObject>) toType);
						ITransferObject to = bean.get(id);
						return to;
					}
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
	@SuppressWarnings("unchecked")
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value == null) {
			return null;
		}
		String string = null;
		try {
			Class<? extends ITransferObject> toType = (Class<? extends ITransferObject>) value.getClass();
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