package com.code.aon.ui.payroll.controller;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.validator.ValidatorException;

import org.apache.commons.beanutils.PropertyUtils;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.payroll.dao.IPayrollAlias;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;

public class PorcentajeController extends LinesController {

	@Override
	public void onAccept(ActionEvent event) {
		boolean bol = isNew();
		super.onAccept(event);
		if ( bol )
			super.onReset( event );
	}

	/**
	 * Validates percent fields that must be between 0% and 100%.
	 * 
	 * @param facesContext
	 * @param uiComponent
	 * @param object
	 * @throws ValidatorException
	 */
	public void validatePercent(FacesContext facesContext, UIComponent uiComponent, Object object) 
				throws ValidatorException {
    	FacesContext ctx = FacesContext.getCurrentInstance();
		try {
			String cId = uiComponent.getId();
			cId = cId.substring( 0, cId.indexOf( '_' ) );
			BigDecimal value = (BigDecimal) object;
			if ( value == null )
				PropertyUtils.setProperty( this.getTo(), cId, new BigDecimal( 0d ) );
		    if ( value.doubleValue() < 0.00) {
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
