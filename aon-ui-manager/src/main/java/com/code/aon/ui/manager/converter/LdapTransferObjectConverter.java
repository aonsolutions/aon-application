package com.code.aon.ui.manager.converter;

import java.io.Serializable;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.dao.ldap.ILdapTransferObject;
import com.code.aon.ldap.NameResolver;
import com.code.aon.ui.form.IController;

public class LdapTransferObjectConverter implements Converter {

	private final static Logger LOGGER = LoggerFactory.getLogger(LdapTransferObjectConverter.class);
	
	private IController controller;
	
	private boolean ldapTransferObject;
	
	public LdapTransferObjectConverter(IController controller) {
		this.controller = controller;
		try {
			this.ldapTransferObject = ILdapTransferObject.class.isAssignableFrom(controller.getManagerBean().getPOJOClass());
		} catch (ManagerBeanException e) {
			LOGGER.error( e.getMessage(), e );
		}
	}
	
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (value != null) {
			try {
				Serializable id = null;
				if ( ldapTransferObject ) {
					id = NameResolver.getName(value);
				} else {
					id = NumberUtils.toInt(value);	
				}
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