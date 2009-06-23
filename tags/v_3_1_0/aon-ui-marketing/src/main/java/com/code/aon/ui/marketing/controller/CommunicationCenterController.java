package com.code.aon.ui.marketing.controller;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;

import com.code.aon.commercial.Target;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.User;
import com.code.aon.marketing.Action;
import com.code.aon.marketing.ActionTarget;
import com.code.aon.marketing.Question;
import com.code.aon.marketing.QuestionValue;
import com.code.aon.marketing.Survey;
import com.code.aon.marketing.SurveyQuestion;
import com.code.aon.marketing.SurveyResponse;
import com.code.aon.marketing.SurveyResponseDetail;
import com.code.aon.marketing.SurveyWorkflow;
import com.code.aon.marketing.TargetProfile;
import com.code.aon.marketing.dao.IMarketingAlias;
import com.code.aon.marketing.enumeration.ActionMediaType;
import com.code.aon.marketing.enumeration.ActionTargetStatus;
import com.code.aon.marketing.enumeration.QuestionType;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryMedia;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.registry.enumeration.AddressType;
import com.code.aon.registry.enumeration.MediaType;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.mailing.MailingManager;
import com.code.aon.ui.util.AonUtil;

public class CommunicationCenterController implements IMarketingConstants {
	
	private Date date;
	
	private Action action;
	
	private Survey survey;
	
	private Target target;
	
	private ActionTarget actionTarget;
	
	private SurveyResponse surveyResponse;
	
	private SurveyResponseDetail response;
	
	private SurveyQuestion surveyQuestion;
	
	private String nextQuestionAction;
	
	private Integer questionValueId;
	
	private List<SelectItem> questionValues;
	
	private RegistryAddress mainAddress;
	
	private RegistryMedia phone;
	
	private RegistryMedia cellular;
	
	private RegistryMedia fax;
	
	private RegistryMedia email;
	
	private RegistryMedia web;	
	
	private boolean targetSelected;
	
	private boolean actionSelected;
	
	private boolean surveySelected;
	
	private int pendingTargets;
	
	public CommunicationCenterController() {
		this.date = new Date();
		this.questionValues = new LinkedList<SelectItem>();
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Action getAction() {
		return action;
	}

	public void setAction(Action action) {
		this.action = ( action != null ) ? action : new Action();
		this.actionSelected = (this.action.getId() != null);
	}

	public Survey getSurvey() {
		return survey;
	}

	public void setSurvey(Survey survey) {
		this.survey = ( survey != null ) ? survey : new Survey();
		this.surveySelected = (this.survey.getId() != null);
	}

	public Target getTarget() {
		return target;
	}

	public void setTarget(Target target) {
		this.target = ( target != null ) ? target : new Target();
		this.targetSelected = (this.target.getId() != null);
	}
	
	public ActionTarget getActionTarget() {
		return actionTarget;
	}

	public void setActionTarget(ActionTarget actionTarget) {
		this.actionTarget = actionTarget;
	}

	public boolean isTargetSelected() {
		return targetSelected;
	}

	public boolean isActionSelected() {
		return actionSelected;
	}
	
	public boolean isSurveySelected() {
		return surveySelected;
	}
	
	public int getPendingTargets() {
		return pendingTargets;
	}

	public void setPendingTargets(int pendingTargets) {
		this.pendingTargets = pendingTargets;
	}

	public boolean isRenderTargetAlias() {
		return ! StringUtils.isEmpty(target.getRegistry().getAlias());
	}

	public RegistryAddress getMainAddress() {
		return mainAddress;
	}
	
	public String getFullAddress() {
		if ( mainAddress != null ) {
			StringBuffer sb = new StringBuffer();
			if (! StringUtils.isEmpty(mainAddress.getAddress()) ) {
				sb.append( mainAddress.getAddress() );
			}
			if (! StringUtils.isEmpty(mainAddress.getAddress2()) ) {
				if ( sb.length() > 0 ) {
					sb.append( " " );
				}
				sb.append( mainAddress.getAddress2() );
			}
			if (! StringUtils.isEmpty(mainAddress.getAddress3()) ) {
				if ( sb.length() > 0 ) {
					sb.append( " " );
				}
				sb.append( mainAddress.getAddress3() );
			}
			return StringUtils.trimToNull(sb.toString());
		}
		return null;
	}

	public RegistryMedia getPhone() {
		return phone;
	}
	
	public RegistryMedia getCellular() {
		return cellular;
	}

	public RegistryMedia getFax() {
		return fax;
	}

	public RegistryMedia getEmail() {
		return email;
	}

	public RegistryMedia getWeb() {
		return web;
	}

	public void onInit( ActionEvent event ) {
		setAction(null);
		setTarget(null);
		setSurvey(null);
		this.surveyResponse = null;
		this.actionTarget = null;
	}
	
	public Question getQuestion() {
		return surveyQuestion.getQuestion();
	}
	
	public Integer getQuestionValueId() {
		return questionValueId;
	}

	public void setQuestionValueId(Integer questionValueId) {
		this.questionValueId = questionValueId;
	}

	public List<SelectItem> getQuestionValues() {
		return questionValues;
	}

	public SurveyResponseDetail getResponse() {
		return response;
	}

	public void onStartSurveyResponse( ActionEvent event ) throws ManagerBeanException {
		this.surveyResponse = new SurveyResponse();
		if ( this.action.getId() != null ) {
			this.surveyResponse.setAction( this.action );
		}
		this.surveyResponse.setSurvey( this.survey );
		this.surveyResponse.setTarget( this.target );
		this.surveyResponse.setCreationDate( new Date() );
		this.surveyResponse.setDate( this.date );
		User user = UserUtils.getInstance().getLoggedUser();
		this.surveyResponse.setUser( user );
		IManagerBean surveyResponseBean = BeanManager.getManagerBean(SurveyResponse.class);
		surveyResponseBean.insert( surveyResponse );
		updateSurveyQuestion( getFirstSurveyQuestion() );
		this.nextQuestionAction = NAVIGATION_COMMUNICATION_CENTER_RESPONSE;
	}
	
	public void onNextQuestion( ActionEvent event ) throws ManagerBeanException {
		saveResponse();
		SurveyQuestion surveyQuestion = getNextSurveyQuestion();
		if ( surveyQuestion != null ) {
			updateSurveyQuestion( surveyQuestion );			
		} else {
			this.nextQuestionAction = NAVIGATION_COMMUNICATION_CENTER;
			finishActionTarget();
			if ( isActionSelected() ) {
				onNextTarget(event);
			} else {
				onInit(event);	
			}			
		}
	}
	
	public String nextQuestionAction() {
		return this.nextQuestionAction;
	}

	private void updateResponseValue() throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(QuestionValue.class);
		QuestionValue questionValue = (QuestionValue) bean.get( this.questionValueId );
		questionValue.copyValues(response);			
	}

	private void finishActionTarget() throws ManagerBeanException {
		if ( this.actionTarget != null ) {
			this.actionTarget.setSurveyResponse(this.surveyResponse);
			this.actionTarget.setStatus(ActionTargetStatus.FINISHED);
			IManagerBean bean = BeanManager.getManagerBean(ActionTarget.class);
			bean.update(this.actionTarget);
		}
	}
	
	private void updateTargetProfile() throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(TargetProfile.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IMarketingAlias.TARGET_PROFILE_TARGET_ID), this.target.getId());
		criteria.addEqualExpression(bean.getFieldName(IMarketingAlias.TARGET_PROFILE_QUESTION_ID), getQuestion().getId());
		TargetProfile targetProfile = null;
		List<ITransferObject> list = bean.getList(criteria);
		if (! list.isEmpty() ) {
			targetProfile = (TargetProfile) list.get(0);
		} else {
			targetProfile = new TargetProfile();
			targetProfile.setTarget( this.target );
			targetProfile.setQuestion( getQuestion() );
		}
		targetProfile.setLastUpdate( new Date() );
		this.response.copyValues(targetProfile);
		bean.insertOrUpdate( targetProfile );
	}
	
	private void saveResponse() throws ManagerBeanException {	
		if ( getQuestion().getType() != QuestionType.INFO ) {
			if (this.questionValueId != null ) {
				updateResponseValue();
			}
			this.response.setSurveyResponse( this.surveyResponse );
			this.response.setQuestion( getQuestion() );
			IManagerBean bean = BeanManager.getManagerBean(SurveyResponseDetail.class);		
			bean.insert( this.response );
			if (! this.response.isNotFilled() ) {
				updateTargetProfile();
			}
		}
	}
	
	private void refreshQuestionValues( SurveyQuestion sq ) throws ManagerBeanException {
		this.questionValues.clear();
		IManagerBean bean = BeanManager.getManagerBean(QuestionValue.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IMarketingAlias.QUESTION_VALUE_QUESTION_ID), sq.getQuestion().getId());
		Iterator<ITransferObject> iter = bean.getList(criteria).iterator();
		while (iter.hasNext()) {
			QuestionValue questionValue = (QuestionValue) iter.next();
			Object value = questionValue.getValue( sq.getQuestion().getType() );
			SelectItem item = new SelectItem(questionValue.getId(), ObjectUtils.toString(value));
			this.questionValues.add(item);
		}	
		this.questionValueId = null;
	}
	
	private void updateSurveyQuestion( SurveyQuestion sq ) throws ManagerBeanException {
		this.surveyQuestion = sq;
		this.response = new SurveyResponseDetail();
		refreshQuestionValues( this.surveyQuestion );
	}

	private SurveyQuestion getFirstSurveyQuestion() throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(SurveyQuestion.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression( bean.getFieldName(IMarketingAlias.SURVEY_QUESTION_SURVEY_ID), this.survey.getId() );
		criteria.addOrder( bean.getFieldName(IMarketingAlias.SURVEY_QUESTION_POSITION) );
		List<ITransferObject> list = bean.getList(criteria, 0, 1);
		if (! list.isEmpty() ) {
			return (SurveyQuestion) list.get(0);
		}
		return null;
	}

	private SurveyWorkflow getSurveyWorkflow( SurveyQuestion surveyQuestion ) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(SurveyWorkflow.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression( bean.getFieldName(IMarketingAlias.SURVEY_WORKFLOW_SURVEY_QUESTION_ID), surveyQuestion.getId() );
		if (this.questionValueId != null ) {
			criteria.addEqualExpression( bean.getFieldName(IMarketingAlias.SURVEY_WORKFLOW_QUESTION_VALUE_ID), this.questionValueId );
		} else {
			String textField = bean.getFieldName(IMarketingAlias.SURVEY_WORKFLOW_TEXT);
			if (this.response.getText() != null) {
				criteria.addEqualExpression( textField, this.response.getDate() );
			} else {
				criteria.addNullExpression(textField);
			}
			String dateField = bean.getFieldName(IMarketingAlias.SURVEY_WORKFLOW_DATE);
			if (this.response.getDate() != null) {
				criteria.addEqualExpression( dateField, this.response.getDate() );			
			} else {
				criteria.addNullExpression(dateField);
			}
			String numberField = bean.getFieldName(IMarketingAlias.SURVEY_WORKFLOW_NUMBER);
			if (this.response.getNumber() != null) {
				criteria.addEqualExpression( numberField, this.response.getNumber() );			
			} else {
				criteria.addNullExpression(numberField);
			}
		}
		List<ITransferObject> list = bean.getList(criteria, 0, 1);
		if (! list.isEmpty() ) {
			return (SurveyWorkflow) list.get(0);
		}
		return null;
	}
	
	private SurveyQuestion getNextSurveyQuestion() throws ManagerBeanException {
		if ( getQuestion().getType() != QuestionType.INFO ) {
			SurveyWorkflow workflow = getSurveyWorkflow( surveyQuestion );
			if ( workflow != null ) {
				return workflow.getNextSurveyQuestion();
			}
		}
		IManagerBean bean = BeanManager.getManagerBean(SurveyQuestion.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression( bean.getFieldName(IMarketingAlias.SURVEY_QUESTION_SURVEY_ID), this.survey.getId() );
		String position = bean.getFieldName(IMarketingAlias.SURVEY_QUESTION_POSITION);
		criteria.addGreaterThanExpression( position, surveyQuestion.getPosition() );
		criteria.addOrder( position );
		List<ITransferObject> list = bean.getList(criteria, 0, 1);
		if (! list.isEmpty() ) {
			return (SurveyQuestion) list.get(0);
		}
		return null;
	}
	
	private RegistryMedia getTargetMedia( Integer id, MediaType type ) throws ManagerBeanException {
		IManagerBean mediaBean = BeanManager.getManagerBean(RegistryMedia.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(mediaBean.getFieldName(IRegistryAlias.REGISTRY_MEDIA_REGISTRY_ID), id);
		criteria.addEqualExpression(mediaBean.getFieldName(IRegistryAlias.REGISTRY_MEDIA_MEDIA_TYPE), type);
		List<ITransferObject> list = mediaBean.getList(criteria);
		if (! list.isEmpty() ) {
			return (RegistryMedia) list.get(0);
		}
		return null;
	}
	
	private RegistryAddress getTargetAddress( Integer id ) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(RegistryAddress.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IRegistryAlias.REGISTRY_ADDRESS_REGISTRY_ID), id);
		criteria.addEqualExpression(bean.getFieldName(IRegistryAlias.REGISTRY_ADDRESS_ADDRESS_TYPE), AddressType.MAIN);
		List<ITransferObject> list = bean.getList(criteria);
		if (! list.isEmpty() ) {
			return (RegistryAddress) list.get(0);
		}
		return null;
	}	
	
	public void initTarget( Target target ) throws ManagerBeanException {
		Integer id = target.getRegistry().getId();
		this.phone = getTargetMedia( id, MediaType.FIXED_PHONE );
		this.cellular = getTargetMedia( id, MediaType.CELLULAR );
		this.fax = getTargetMedia( id, MediaType.FAX );
		this.email = getTargetMedia( id, MediaType.EMAIL );
		this.web = getTargetMedia( id, MediaType.WEB );
		this.mainAddress = getTargetAddress(id);
	}
	
	public void onNextTarget( ActionEvent event ) throws ManagerBeanException {
		if ( this.action.getMediaType() == ActionMediaType.PHONE ) {
			List<ActionTarget> targets = getActionTargets(true);
			if (! targets.isEmpty() ) {
				this.actionTarget = targets.get(0);
				setTarget( this.actionTarget.getTarget() );
				initTarget(this.target);
			} else {
				onInit(event);
			}
		} else {
			setTarget(null);
		}
		refreshPendingTargets();		
	}
	
	public void onUpdateActionTarget( ActionEvent event ) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(ActionTarget.class);
		bean.update(this.actionTarget);
		onNextTarget(event);
	}

	public void onActionLookupChange(LookupChangeEvent event) {
		this.actionSelected = (event.getNewValue() != null);	
		if (this.actionSelected) {
			Action action = (Action) event.getNewValue();
			setAction(action);
			try {				
				Survey survey = null;
				if (action.getSurvey().getId() != null) {
					IManagerBean bean = BeanManager.getManagerBean(Survey.class);
					survey = (Survey) bean.get(action.getSurvey().getId());
				}
				setSurvey(survey);
				onNextTarget(null);
			} catch (ManagerBeanException e) {
				AonUtil.addErrorMessage(e.getMessage());
				throw new AbortProcessingException(e);
			}
		} else {
			setActionTarget(null);
		}
	}

	public void onTargetLookupChange(LookupChangeEvent event) {
		this.targetSelected = (event.getNewValue() != null);
		if (this.targetSelected) {
			try {
				Target newTarget = (Target) event.getNewValue();
				initTarget(newTarget);
			} catch (ManagerBeanException e) {
				AonUtil.addErrorMessage(e.getMessage());
				throw new AbortProcessingException(e);
			}
		}
	}

	public void onSurveyLookupChange(LookupChangeEvent event) {
		this.surveySelected = (event.getNewValue() != null);
	}

	private Criteria getPendingTargetsCriteria( IManagerBean bean, boolean onlyCount ) throws ManagerBeanException {
		Criteria criteria = new Criteria();
		String id = bean.getFieldName(IMarketingAlias.ACTION_TARGET_ID);
		criteria.addOrder( id );
		if ( !onlyCount && (this.actionTarget != null) ) {
			criteria.addGreaterThanExpression( id, this.actionTarget.getId() );	
		}
		criteria.addEqualExpression(bean.getFieldName(IMarketingAlias.ACTION_TARGET_ACTION_ID), this.action.getId());
		String status = bean.getFieldName(IMarketingAlias.ACTION_TARGET_STATUS);
		Expression expression1 = ExpressionUtilities.getNotEqualExpression(status, ActionTargetStatus.FINISHED);
		criteria.addExpression(expression1);
		Expression expression2 = ExpressionUtilities.getNotEqualExpression(status, ActionTargetStatus.SENT);
		criteria.addExpression(expression2);
		return criteria;
	}
	
	@SuppressWarnings("unchecked")
	public List<ActionTarget> getActionTargets( boolean onlyFirst ) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(ActionTarget.class);
		Criteria criteria = getPendingTargetsCriteria(bean, false);
		List list = onlyFirst ? bean.getList(criteria, 0, 1) : bean.getList(criteria);
		return list;
	}

	@SuppressWarnings("unchecked")
	private void refreshPendingTargets() throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(ActionTarget.class);
		Criteria criteria = getPendingTargetsCriteria(bean, true);
		setPendingTargets(bean.getCount(criteria));
	}
	
	@SuppressWarnings({"unchecked", "unused"})
	public void onGenerateTargetMailing(ActionEvent event) throws ManagerBeanException {
        List<ActionTarget> targets = getActionTargets(false);
        MailingManager.generateMailing(targets);
        IManagerBean bean = BeanManager.getManagerBean(ActionTarget.class);
        for( ActionTarget target : targets ) {
        	target.setStatus(ActionTargetStatus.FINISHED);
        	bean.update(target);
        }
	}		
	
}
