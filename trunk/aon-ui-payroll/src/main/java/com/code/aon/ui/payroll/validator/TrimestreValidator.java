package com.code.aon.ui.payroll.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import com.code.aon.ui.util.AonUtil;

public class TrimestreValidator implements Validator {

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
		
		System.out.println("***********************  TrimestreValidator *******************");
		
		/*
		 try {
			String cId = uiComponent.getId();
			//cId = cId.substring( 0, cId.indexOf( '_' ) );
			Integer value = (Integer) object;
			ITransferObject to = FormUtil.getController( cId ).getTo();
			
			
			if(to == null){
		    	FacesMessage fm = AonUtil.getMessage( ctx, "aon_payroll_null_to", null );
				throw new ValidatorException( fm );
		    }
		    
			
			if ( value == null )
				PropertyUtils.setProperty( to, cId, new BigDecimal( 0d ) );
			
		    if ( value.intValue() < 0 || value.intValue() > 4) {
		    	FacesMessage fm = AonUtil.getMessage( ctx, "trimestre fuera de rango", null );
				throw new ValidatorException( fm );
		    }
		   
		} catch (IllegalAccessException e) {
			throw new ValidatorException( AonUtil.getMessage( ctx, e.getMessage(), null ) );
		} catch (InvocationTargetException e) {
			throw new ValidatorException( AonUtil.getMessage( ctx, e.getMessage(), null ) );
		} catch (NoSuchMethodException e) {
			throw new ValidatorException( AonUtil.getMessage( ctx, e.getMessage(), null ) );
		}
		*/
		
		
		Integer value = (Integer) object;
		if ( value.intValue() < 0 || value.intValue() > 4) {
	    	FacesMessage fm = AonUtil.getMessage( ctx, "aon_payroll_rango_trimestre", null );
			throw new ValidatorException( fm );
	    }
	
	
	}

}
