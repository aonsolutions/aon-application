package com.code.aon.ui.audit.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.ui.audit.ApplicationOption;
import com.code.aon.ui.audit.controller.ApplicationOptionController;
import com.code.aon.ui.audit.controller.IAuditConstants;
import com.code.aon.ui.util.AonUtil;

public class ApplicationOptionConverter implements Converter, IAuditConstants {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(ApplicationOptionConverter.class);

	@Override
	public Object getAsObject(FacesContext context, UIComponent component,
			String value) {
		if (! StringUtils.isEmpty(value) ) {
			ApplicationOptionController aoc = (ApplicationOptionController) AonUtil.getRegisteredBean(APPLICATION_OPTION_CONTROLLER_NAME);
			ApplicationOption option = aoc.getOptionMap().get(value);
			return option;			
		}
		return null;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component,
			Object value) {
		if (value == null) {
			return null;
		}		
		if ( value instanceof ApplicationOption ) {
			return ((ApplicationOption) value).getAction();	
		}
		LOGGER.warn( "value isn't ApplicationOption: {}", value );
		return value.toString();
	}
	
}
