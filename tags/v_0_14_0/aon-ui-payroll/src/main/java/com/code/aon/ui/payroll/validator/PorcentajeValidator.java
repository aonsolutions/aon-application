package com.code.aon.ui.payroll.validator;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.apache.commons.beanutils.PropertyUtils;

import com.code.aon.common.ITransferObject;
import com.code.aon.ui.util.AonUtil;

public class PorcentajeValidator implements Validator {

	/**
	 * Validates percent fields that must be between 0% and 100%.
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
		
		try {
			String cId = uiComponent.getId();
			cId = cId.substring( 0, cId.indexOf( '_' ) );
			BigDecimal value = (BigDecimal) object;
			ITransferObject to = AonUtil.getController( cId ).getTo();
			
			if(to == null){
		    	FacesMessage fm = AonUtil.getMessage( ctx, "aon_payroll_null_to", null );
				throw new ValidatorException( fm );
		    }
			if ( value == null )
				PropertyUtils.setProperty( to, cId, new BigDecimal( 0d ) );
		    if ( value.doubleValue() < 0.00) {
		    	FacesMessage fm = AonUtil.getMessage( ctx, "aon_payroll_1433", null );
				throw new ValidatorException( fm );
		    }
		    if ( value.doubleValue() > 100) {
		    	FacesMessage fm = AonUtil.getMessage( ctx, "aon_payroll_1433", null );
				throw new ValidatorException( fm );
		    }
		} catch (IllegalAccessException e) {
			throw new ValidatorException( AonUtil.getMessage( ctx, e.getMessage(), null ) );
		} catch (InvocationTargetException e) {
			throw new ValidatorException( AonUtil.getMessage( ctx, e.getMessage(), null ) );
		} catch (NoSuchMethodException e) {
			throw new ValidatorException( AonUtil.getMessage( ctx, e.getMessage(), null ) );
		}
		
	}

}
