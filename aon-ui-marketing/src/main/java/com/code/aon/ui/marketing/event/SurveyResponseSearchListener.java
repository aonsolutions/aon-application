package com.code.aon.ui.marketing.event;

import com.code.aon.commercial.Target;
import com.code.aon.common.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.User;
import com.code.aon.marketing.MarketingAction;
import com.code.aon.marketing.Survey;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.form.event.ControllerSearchListener;
import com.esferalia.aon.entity.IEntityAlias;

public class SurveyResponseSearchListener extends ControllerSearchListener {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private Survey survey;
	
	private Target target;
	
	private MarketingAction action;
	
	private User user;
	
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

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
		IManagerBean maBean = BeanManager.getManagerBean(MarketingAction.class);
		setAction( (MarketingAction) maBean.createNewTo() );
		IManagerBean surveyBean = BeanManager.getManagerBean(Survey.class);
		setSurvey( (Survey) surveyBean.createNewTo() );
		IManagerBean targetBean = BeanManager.getManagerBean(Target.class);
		setTarget( (Target) targetBean.createNewTo() );
		IManagerBean userBean = BeanManager.getManagerBean(User.class);
		setUser( (User) userBean.createNewTo() );
	}
	
	@Override
	protected void completeCriteria( Criteria criteria ) throws ManagerBeanException, ExpressionException {
		if ( getSurvey()!=null && getSurvey().getId()!=null ) {
			String alias = getFieldName(IEntityAlias.SURVEY_RESPONSE_SURVEY_ID);
			criteria.addEqualExpression(alias, getSurvey().getId());			
		}
		if ( getTarget()!=null && getTarget().getId()!=null ) {
			String alias = getFieldName(IEntityAlias.SURVEY_RESPONSE_TARGET_ID);
			criteria.addEqualExpression(alias, getTarget().getId());			
		}
		if ( getAction()!=null && getAction().getId()!=null ) {
			String alias = getFieldName(IEntityAlias.SURVEY_RESPONSE_ACTION_ID);
			criteria.addEqualExpression(alias, getAction().getId());			
		}
		if ( getUser()!=null && getUser().getId()!=null ) {
			String alias = getFieldName(IEntityAlias.SURVEY_RESPONSE_USER_ID);
			criteria.addEqualExpression(alias, getUser().getId());			
		}
	}	
}