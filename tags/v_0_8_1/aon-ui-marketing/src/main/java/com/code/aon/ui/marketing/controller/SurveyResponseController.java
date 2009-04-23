package com.code.aon.ui.marketing.controller;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import com.code.aon.commercial.Target;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.marketing.Action;
import com.code.aon.marketing.ActionTarget;
import com.code.aon.marketing.Survey;
import com.code.aon.marketing.dao.IMarketingAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.util.AonUtil;

/**
 * Controller used in the offer maintenance.
 */
public class SurveyResponseController extends BasicController implements IMarketingConstants {
	
	private static final Logger LOGGER = Logger.getLogger(SurveyResponseController.class.getName());

	private Survey survey;
	
	private Target target;
	
	private Action action;
	
	public void onSelectSurveyResponse( ActionEvent event ) throws NumberFormatException, ManagerBeanException {
        FacesContext context = FacesContext.getCurrentInstance();
		String id = context.getExternalContext().getRequestParameterMap().get("surveyResponseId");		
		Criteria oldCriteria = getCriteria();
		clearCriteria();
		getCriteria().addEqualExpression( getFieldName(IMarketingAlias.SURVEY_RESPONSE_ID), Integer.valueOf(id));
		onSearch( event );
		onSelectFirst( event );
		setCriteria(oldCriteria);
	}
		
	public Survey getSurvey() {
		return survey;
	}

	public void setSurvey(Survey survey) {
		this.survey = survey;
	}

	public Target getTarget() {
		return target;
	}

	public void setTarget(Target target) {
		this.target = target;
	}

	public Action getAction() {
		return action;
	}

	public void setAction(Action action) {
		this.action = action;
	}

	@Override
	public void onEditSearch(ActionEvent event) {
		setSurvey( new Survey() );
		setTarget( new Target() );
		setAction( new Action() );
		super.onEditSearch(event);
	}
	
	@Override
	public void onSearch(ActionEvent event) {
		try {
			completeCriteria();
		} catch (ManagerBeanException e) {
			LOGGER.log( Level.SEVERE, e.getMessage(), e );
		}
		super.onSearch(event);
	}
	
	public void completeCriteria() throws ManagerBeanException {
		if ( (getSurvey() != null) && (getSurvey().getId() != null) ) {
			String alias = getFieldName(IMarketingAlias.SURVEY_RESPONSE_SURVEY_ID);
			getCriteria().addEqualExpression(alias, getSurvey().getId());			
		}
		if ( (getTarget() != null) && (getTarget().getId() != null) ) {
			String alias = getFieldName(IMarketingAlias.SURVEY_RESPONSE_TARGET_ID);
			getCriteria().addEqualExpression(alias, getTarget().getId());			
		}
		if ( (getAction() != null) && (getAction().getId() != null) ) {
			String alias = getFieldName(IMarketingAlias.SURVEY_RESPONSE_ACTION_ID);
			getCriteria().addEqualExpression(alias, getAction().getId());			
		}
	}
	
}