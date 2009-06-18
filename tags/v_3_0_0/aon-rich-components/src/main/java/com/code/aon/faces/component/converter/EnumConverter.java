package com.code.aon.faces.component.converter;

import javax.faces.component.StateHolder;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;

import com.code.aon.common.enumeration.IResourceable;
import com.code.aon.ui.converter.EnumLocaleConverter;
import com.sun.facelets.util.ReflectionUtil;

public class EnumConverter extends EnumLocaleConverter implements StateHolder {

	private Class<? extends IResourceable> enumClass;	

	public String getEnum() {
		return enumClass.getName();
	}

	public void setEnum(String enumClass) {
		try {
			this.enumClass = ReflectionUtil.forName(enumClass);
		} catch (Exception e) {
			throw new AbortProcessingException("Couldn't Lazily instantiate Enum Class", e);
		}
	}	
	
	@Override
	protected Class getEnumClass(FacesContext ctx, UIComponent comp) {
		return this.enumClass;
	}	
	
    // ----------------------------------------------------- StateHolder Methods
	
	@Override
	public void restoreState(FacesContext context, Object state) {
        Object values[] = (Object[]) state;
        enumClass = (Class) values[0];		
	}

	@Override
	public Object saveState(FacesContext context) {
        Object values[] = new Object[1];
        values[0] = this.enumClass;
        return (values);
	}	
	
	private boolean transientFlag = false;
	
	@Override
	public boolean isTransient() {
		return transientFlag;
	}

	@Override
	public void setTransient(boolean newTransientValue) {
		this.transientFlag = newTransientValue;
	}
	
}
