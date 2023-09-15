package com.code.aon.ui.common.converter;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import jakarta.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.Classpath;

/**
 * 
 *
 */
public class MappedTransferObjectConverter implements Converter {

	private final static Logger LOGGER = LoggerFactory.getLogger(MappedTransferObjectConverter.class);
	private static final String TO_CONVERTER_MAP_FILE = "to_converter_map.properties";
	private static Map<Class<?>,Class<?>> entityInterfaces; 
	
	static {
		initializeMap();
	}
	
	private static void initializeMap() {
		try {
			ClassLoader cl = Thread.currentThread().getContextClassLoader();
			URL[] urls = Classpath.search(cl, "META-INF/", TO_CONVERTER_MAP_FILE);
			Properties props = new Properties();
			if (!ArrayUtils.isEmpty(urls)) {
				Properties pr = new Properties();
				for (URL url : urls) {
					pr.load(url.openStream());
					props.putAll(pr);
				}
			}
			Set<Object> set = props.keySet();
			for (Object k : set ){
				String key = (String) k;
				String value = props.getProperty(key);
				try {
					Class<?> keyClass = Class.forName(key);	
					Class<?> valueClass = Class.forName(value);
					MappedTransferObjectConverter.getEntityInterfaces().put(keyClass, valueClass);
				} catch (ClassNotFoundException e) {
					LOGGER.debug(e.getMessage(), e);
				}
			}
		} catch (IOException e) {
			LOGGER.debug(e.getMessage(), e);
		}
	}

	private static Map<Class<?>,Class<?>> getEntityInterfaces() {
		if (entityInterfaces == null) {
			entityInterfaces = new HashMap<Class<?>, Class<?>>();
		}
		return entityInterfaces;
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
	
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		Class<? extends ITransferObject> toType = getToType( context,  component );
		if (toType != null) {
			if (value != null) {
				try {
					Serializable id = getId(value);
					if ( id != null ) {
						IManagerBean bean = BeanManager.getManagerBean( toType );
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

	@SuppressWarnings("unchecked")
	private Class<? extends ITransferObject> getToType(FacesContext context, UIComponent component) {
		ValueExpression vb = component.getValueExpression("value");
		Class<?> toType = (vb != null) ? vb.getType(context.getELContext()) : null;
		if (getEntityInterfaces().containsKey(toType)) {
			 toType = getEntityInterfaces().get(toType);
		}
		return (Class<? extends ITransferObject>) toType;
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