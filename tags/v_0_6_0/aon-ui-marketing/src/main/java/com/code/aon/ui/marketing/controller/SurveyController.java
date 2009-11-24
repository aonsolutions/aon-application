package com.code.aon.ui.marketing.controller;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.ActionEvent;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.marketing.dao.IMarketingAlias;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.form.BasicController;

/**
 * Controller used in the offer maintenance.
 */
public class SurveyController extends BasicController {
	
	private static final Logger LOGGER = Logger.getLogger(SurveyController.class.getName());
	
	private boolean statusActive;
	
	private boolean statusInactive;
	
	@Override
	public void onEditSearch(ActionEvent event) {
		initializeStatusFilter();
		super.onEditSearch(event);
	}
	
	@Override
	public void onSearch(ActionEvent event) {
		try {
			completeStatusCriteria();
		} catch (ManagerBeanException e) {
			LOGGER.log( Level.SEVERE, e.getMessage(), e );
		}
		super.onSearch(event);
	}

	private void initializeStatusFilter() {
		setStatusActive(true);
		setStatusInactive(true);
	}
	
	private void completeStatusCriteria() throws ManagerBeanException {
		if (isStatusActive() || isStatusInactive()) {
			Expression expToAdd = null;
			String alias = getFieldName(IMarketingAlias.SURVEY_ACTIVE);
			if ( isStatusActive() ) {
				expToAdd = ExpressionUtilities.getEqualExpression(alias,
						Boolean.TRUE);
			}
			if ( isStatusInactive() ) {
				Expression exp  = ExpressionUtilities.getEqualExpression(alias,
						Boolean.FALSE);
				if ( expToAdd != null ) {
					expToAdd = ExpressionUtilities.getOrExpression(expToAdd, exp);
				} else {
					expToAdd = exp;
				}
			}
			getCriteria().addExpression(expToAdd);
		}		
	}
	
	// -------------------------------------------------
	// Getters y setters para los campos de la búsqueda.
	// -------------------------------------------------

	public boolean isStatusActive() {
		return statusActive;
	}

	public void setStatusActive(boolean statusActive) {
		this.statusActive = statusActive;
	}

	public boolean isStatusInactive() {
		return statusInactive;
	}

	public void setStatusInactive(boolean statusInactive) {
		this.statusInactive = statusInactive;
	}
		
}