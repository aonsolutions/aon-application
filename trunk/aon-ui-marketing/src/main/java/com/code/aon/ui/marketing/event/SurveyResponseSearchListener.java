package com.code.aon.ui.marketing.event;

import com.code.aon.commercial.Target;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.marketing.MarketingAction;
import com.code.aon.marketing.Survey;
import com.code.aon.marketing.dao.IMarketingAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.form.event.ControllerSearchListener;

public class SurveyResponseSearchListener extends ControllerSearchListener {

	private Survey survey;
	
	private Target target;
	
	private MarketingAction action;

	public Survey getSurvey() {
		return survey;
	}

	public void setSurvey(Survey survey) {
		this.survey = survey;
	}

	public MarketingAction getAction() {
		return action;
	}

	public void setAction(MarketingAction action) {
		this.action = action;
	}

	public Target getTarget() {
		return target;
	}

	public void setTarget(Target target) {
		this.target = target;
	}

	@Override
	protected void init() throws ManagerBeanException {
		setAction(new MarketingAction());
		setSurvey(new Survey());
		setTarget(new Target());
	}
	
	@Override
	protected void completeCriteria() throws ManagerBeanException, ExpressionException {
		Criteria criteria = getController().getCriteria();
		if ( (getSurvey() != null) && (getSurvey().getId() != null) ) {
			String alias = getFieldName(IMarketingAlias.SURVEY_RESPONSE_SURVEY_ID);
			criteria.addEqualExpression(alias, getSurvey().getId());			
		}
		if ( (getTarget() != null) && (getTarget().getId() != null) ) {
			String alias = getFieldName(IMarketingAlias.SURVEY_RESPONSE_TARGET_ID);
			criteria.addEqualExpression(alias, getTarget().getId());			
		}
		if ( (getAction() != null) && (getAction().getId() != null) ) {
			String alias = getFieldName(IMarketingAlias.SURVEY_RESPONSE_ACTION_ID);
			criteria.addEqualExpression(alias, getAction().getId());			
		}
	}	
}