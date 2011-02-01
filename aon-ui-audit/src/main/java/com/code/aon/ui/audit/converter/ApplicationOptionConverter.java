package com.code.aon.ui.audit.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import com.code.aon.ui.audit.ApplicationOption;
import com.code.aon.ui.audit.controller.ApplicationOptionController;
import com.code.aon.ui.audit.controller.IAuditConstants;
import com.code.aon.ui.util.AonUtil;

public class ApplicationOptionConverter implements Converter, IAuditConstants {

	@Override
	public Object getAsObject(FacesContext context, UIComponent component,
			String value) {
		ApplicationOptionController aoc = (ApplicationOptionController) AonUtil.getRegisteredBean(APPLICATION_OPTION_CONTROLLER_NAME);
		ApplicationOption option = aoc.getOptionMap().get(value);
		return option;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component,
			Object value) {
		if (value == null) {
			return null;
		}		
		return ((ApplicationOption) value).getAction();
	}
	
}
