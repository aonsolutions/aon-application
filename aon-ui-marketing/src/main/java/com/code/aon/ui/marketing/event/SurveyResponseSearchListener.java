package com.code.aon.ui.marketing.event;

import java.util.List;

import javax.faces.model.SelectItem;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.User;
import com.code.aon.marketing.MarketingAction;
import com.code.aon.marketing.Survey;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.registry.Question;
import com.code.aon.registry.QuestionValue;
import com.code.aon.registry.Registry;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.form.event.ControllerSearchListener;
import com.code.aon.ui.registry.controller.RegistryCollectionsController;
import com.esferalia.aon.entity.IEntityAlias;

public class SurveyResponseSearchListener extends ControllerSearchListener {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private Survey survey;
	
	private Registry registry;
	
	private MarketingAction action;
	
	private User user;
	
	private Question question;

	private QuestionValue questionValue;
	
	private List<SelectItem> questionValues;
	
	private Integer questionValueId;
	
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

	public Registry getRegistry() {
		return registry;
	}

	public void setRegistry(Registry registry) {
		this.registry = registry;
	}

	public Question getQuestion() {
		return question;
	}

	public void setQuestion(Question question) {
		this.question = question;
	}
	
	public boolean isQuestionResolved() {
		return (getQuestion() != null) && (getQuestion().getId() != null);
	}	
	
	public Integer getQuestionValueId() {
		return questionValueId;
	}

	public void setQuestionValueId(Integer questionValueId) {
		this.questionValueId = questionValueId;
	}
	
	public QuestionValue getQuestionValue() {
		return questionValue;
	}

	public void setQuestionValue(QuestionValue questionValue) {
		this.questionValue = questionValue;
	}	

	public List<SelectItem> getQuestionValues() {
		return questionValues;
	}
	
	public void setQuestionValues(List<SelectItem> questionValues) {
		this.questionValues = questionValues;
	}

	public void questionChanged( LookupChangeEvent event ) throws ManagerBeanException {
		Question question = (Question) event.getNewValue();
		if ( event.getNewValue() != null ) {
			QuestionValue qv = new QuestionValue();
			qv.setQuestion(question);
			setQuestionValue( qv );
			questionValues = RegistryCollectionsController.getQuestionValues(question);
		} else {
			resetQuestionValue();
		}
	}
	
	private void resetQuestionValue() {
		setQuestionValue(null);
		setQuestionValueId(null);
		setQuestionValues(null);		
	}

	private void completeCriteria( Criteria criteria, QuestionValue qv ) {
		switch ( qv.getQuestion().getType() ) {
			case BOOLEAN:
			case NUMBER:
				criteria.addEqualExpression("SurveyResponse.details.number", qv.getNumber());
				break;
			case DATE:
				criteria.addEqualExpression("SurveyResponse.details.date", qv.getDate());
				break;
			case TEXT:
				Expression exp = ExpressionUtilities.getLikeExpression("SurveyResponse.details.text", "%"+qv.getText()+"%");
				criteria.addExpression(exp);
				break;
			case INFO:
				break;
		}		
	}	
	
	@Override
	protected void init() throws ManagerBeanException {
		IManagerBean maBean = BeanManager.getManagerBean(MarketingAction.class);
		setAction( (MarketingAction) maBean.createNewTo() );
		IManagerBean surveyBean = BeanManager.getManagerBean(Survey.class);
		setSurvey( (Survey) surveyBean.createNewTo() );
		IManagerBean registryBean = BeanManager.getManagerBean(Registry.class);
		setRegistry( (Registry) registryBean.createNewTo() );
		IManagerBean userBean = BeanManager.getManagerBean(User.class);
		setUser( (User) userBean.createNewTo() );
		IManagerBean questionBean = BeanManager.getManagerBean(Question.class);
		setQuestion( (Question) questionBean.createNewTo() );
		resetQuestionValue();
	}
	
	@Override
	protected void completeCriteria( Criteria criteria ) throws ManagerBeanException, ExpressionException {
		if ( getSurvey()!=null && getSurvey().getId()!=null ) {
			String alias = getFieldName(IEntityAlias.SURVEY_RESPONSE_SURVEY_ID);
			criteria.addEqualExpression(alias, getSurvey().getId());			
		}
		if ( getRegistry()!=null && getRegistry().getId()!=null ) {
			String alias = getFieldName(IEntityAlias.SURVEY_RESPONSE_REGISTRY_ID);
			criteria.addEqualExpression(alias, getRegistry().getId());			
		}
		if ( getAction()!=null && getAction().getId()!=null ) {
			String alias = getFieldName(IEntityAlias.SURVEY_RESPONSE_ACTION_ID);
			criteria.addEqualExpression(alias, getAction().getId());			
		}
		if ( getUser()!=null && getUser().getId()!=null ) {
			String alias = getFieldName(IEntityAlias.SURVEY_RESPONSE_USER_ID);
			criteria.addEqualExpression(alias, getUser().getId());			
		}
		if ( isQuestionResolved() ) {
			criteria.addEqualExpression("SurveyResponse.details.question.id", getQuestion().getId());	
			if ( getQuestionValueId() != null ) {
				IManagerBean bean = BeanManager.getManagerBean(QuestionValue.class);
				QuestionValue qv = (QuestionValue) bean.get(getQuestionValueId());
				if ( qv != null ) {
					completeCriteria(criteria, qv);
				}
			} else if (! getQuestionValue().isNotFilled() ) {
				completeCriteria(criteria, getQuestionValue());
			}
		}		
	}	
}