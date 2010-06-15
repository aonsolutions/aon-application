package com.code.aon.ui.payroll.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import com.code.aon.ui.util.AonUtil;

public class MonthValidator implements Validator {

	/**
	 * Validates the letter of DNI
	 * 
	 * @param facesContext
	 * @param uiComponent
	 * @param object
	 * @throws ValidatorException
	 */
	@Override
	public void validate(FacesContext facesContext, UIComponent uiComponent, Object object)
			throws ValidatorException {
		FacesContext ctx = FacesContext.getCurrentInstance();
		
		System.out.println("***********************  MonthValidator *******************");
		
		Integer value = (Integer) object;
		if ( value.intValue() < 0 || value.intValue() > 12) {
	    	FacesMessage fm = AonUtil.getMessage( ctx, "aon_payroll_rango_mes", null );
			throw new ValidatorException( fm );
	    }
	
	
	}

}
