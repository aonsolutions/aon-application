package com.code.aon.ui.commercial.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.el.ValueBinding;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.ClassUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;

public class ITransferObjectConverter implements Converter {

    public Object getAsObject(FacesContext context, UIComponent component, String value) {
   		ValueBinding vb = component.getValueBinding("value");
   		Class toType = vb == null ? null : vb.getType(context);
   		if ( toType != null) {
   			if ( value != null ) {
	   			try {
					IManagerBean bean = BeanManager.getManagerBean( toType );
					ITransferObject to = bean.get( Integer.valueOf(value) );
					return to;
				} catch (ManagerBeanException e) {
					throw new ConverterException( "Unable to find " + ClassUtils.getShortClassName(toType) + " with id " + value );
				}
   			}
   			return null;
   		}
   		throw new ConverterException( "Unable to find selectItems with TransferObject values.");
    }

    public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value == null) {
			return null;
		}
		String id = null;
		try {
			id = BeanUtils.getProperty( value, "id" );
		} catch (Throwable th) {
			throw new ConverterException( "Unable to get id from " + value );
		}
		return id;
    }

}