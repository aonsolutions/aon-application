package com.code.aon.ui.marketing.controller;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.marketing.Question;
import com.code.aon.marketing.dao.IMarketingAlias;
import com.code.aon.marketing.enumeration.QuestionType;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.converter.EnumLocaleConverter;
import com.code.aon.ui.form.AbstractPojoController;
import com.code.aon.ui.form.BasicController;

/**
 * Controller used in the offer maintenance.
 */
public class QuestionController extends BasicController {
	
	private static final Logger LOGGER = Logger.getLogger(QuestionController.class.getName());
	
	private boolean statusActive;
	
	private boolean statusInactive;
	
	public Question getQuestion() {
		return (Question) getTo();
	}
	
	public boolean isBoolean() {
		return getQuestion().getType() == QuestionType.BOOLEAN;
	}

	public boolean isDate() {
		return getQuestion().getType() == QuestionType.DATE;
	}

	public boolean isText() {
		return getQuestion().getType() == QuestionType.TEXT;
	}

	public boolean isNumber() {
		return getQuestion().getType() == QuestionType.NUMBER;
	}
	
	public String getTextAbbreviated() {
		Question question = (Question) getSelectedTO();
		return StringUtils.abbreviate(question.getText(), 60);
	}
	
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
			String alias = getFieldName(IMarketingAlias.QUESTION_ACTIVE);
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
	
	public void addEqualExpression(ValueChangeEvent event) throws ManagerBeanException {
		if (event.getNewValue() != null) {
			String fieldName = getFieldName(event.getComponent().getId());
			getCriteria().addEqualExpression(fieldName, event.getNewValue());
		}
	}
	
	public Converter getTypeConverter() {
		return new EnumLocaleConverter() {

			@Override
			protected Class getEnumClass(FacesContext ctx, UIComponent comp) {
				return QuestionType.class;
			}
			
		};
	}
	
}