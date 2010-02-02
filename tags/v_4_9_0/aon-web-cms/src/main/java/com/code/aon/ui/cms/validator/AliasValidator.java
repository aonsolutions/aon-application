package com.code.aon.ui.cms.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AliasValidator implements Validator {

	private final static Logger LOGGER = LoggerFactory.getLogger(AliasValidator.class);
	
	public AliasValidator() {
	}

	public void validate(FacesContext context, UIComponent component,
			Object value) throws ValidatorException {
		String strValue = (String) value;
		if (!strValue.matches("[0-9a-zA-Z_-]+")){
			//component.getAttributes().get("label")
			throwException("Alias must contain only 0-9,A-Z,a-z,-,_");
		}
	}

	private void throwException(String errMessage) {
		FacesMessage message = new FacesMessage();
		message.setDetail(errMessage);
		message.setSummary(errMessage);
		message.setSeverity(FacesMessage.SEVERITY_ERROR);
		throw new ValidatorException(message);
	}

	public static void main(String[] args) {
		String strValue = "A1_-12-31_23dDDFFSDFfasdfsadffsdf";
		if (!strValue.matches("[0-9a-zA-Z_-]+")){
			LOGGER.info("ERROR");
		}
	}
}
