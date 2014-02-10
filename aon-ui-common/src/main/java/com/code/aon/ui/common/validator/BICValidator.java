package com.code.aon.ui.common.validator;

import static javax.faces.application.FacesMessage.SEVERITY_ERROR;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import com.code.aon.ui.common.ICommonMessages;
import com.code.aon.ui.util.AonUtil;

public class BICValidator implements Validator {

	@Override
	public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
		String bic = (String) value;
		Pattern pattern = Pattern.compile("[A-Z]{6}[A-Z2-9][A-NP-Z0-9]([A-Z0-9]{3})?");
		Matcher matcher = pattern.matcher(bic);
		if ( matcher.matches() ) {
			if ( bic.length() == 8 ) {
				String message = AonUtil.getMessage(ICommonMessages.BIC_ONLY_8);
				FacesMessage fm = new FacesMessage(message);
				fm.setSeverity(SEVERITY_ERROR);
				throw new ValidatorException(fm);									
			}
		} else {
			String message = AonUtil.getMessage(ICommonMessages.BIC_INVALID_FORMAT);
			FacesMessage fm = new FacesMessage(message);
			fm.setSeverity(SEVERITY_ERROR);
			throw new ValidatorException(fm);					
		}		
	}
	

}
