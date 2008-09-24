package com.code.aon.ui.marketing.controller;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

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
import com.code.aon.marketing.Question;
import com.code.aon.marketing.QuestionValue;
import com.code.aon.marketing.Survey;
import com.code.aon.marketing.SurveyQuestion;
import com.code.aon.marketing.SurveyResponse;
import com.code.aon.marketing.SurveyResponseDetail;
import com.code.aon.marketing.TargetProfile;
import com.code.aon.marketing.dao.IMarketingAlias;
import com.code.aon.marketing.enumeration.QuestionType;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryMedia;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.registry.enumeration.AddressType;
import com.code.aon.registry.enumeration.MediaType;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class CommunicationCenterController extends ControllerAdapter {
	
	private static final Logger LOGGER = Logger.getLogger(CommunicationCenterController.class.getName());
	
	private Date date;
	
	private Survey survey;
	
	private Target target;
	
	private SurveyResponse surveyResponse;
	
	private SurveyResponseDetail response;
	
	private SurveyQuestion surveyQuestion;
	
	private Integer questionValueId;
	
	private List<SelectItem> questionValues;
	
	private SurveyQuestion nextQuestion;
	
	private RegistryAddress mainAddress;
	
	private RegistryMedia phone;
	
	private RegistryMedia cellular;
	
	private RegistryMedia fax;
	
	private RegistryMedia email;
	
	private RegistryMedia web;	
	
	private boolean targetSelected;
	
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
	
	public boolean isTargetSelected() {
		return targetSelected;
	}

	public void setTargetSelected(boolean targetSelected) {
		this.targetSelected = targetSelected;
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

	public void onInitSurveyResponse( ActionEvent event ) {
		resetTarget();
		if ( this.survey == null ) {
			this.survey = new Survey();	
		}
		this.surveyResponse = null;
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

	public boolean isLastQuestion() {
		return this.nextQuestion == null;
	}
	
	public SurveyResponseDetail getResponse() {
		return response;
	}

	public void onStartSurveyResponse( ActionEvent event ) throws ManagerBeanException {
		this.surveyResponse = new SurveyResponse();
		this.surveyResponse.setSurvey( this.survey );
		this.surveyResponse.setTarget( this.target );
		this.surveyResponse.setCreationDate( new Date() );
		this.surveyResponse.setDate( this.date );
		User user = UserUtils.getInstance().getLoggedUser();
		this.surveyResponse.setUser( user );
		IManagerBean surveyResponseBean = BeanManager.getManagerBean(SurveyResponse.class);
		surveyResponseBean.insert( surveyResponse );
		updateSurveyResponse( getFirstSurveyQuestion() );
	}
	
	public void onNextQuestion( ActionEvent event ) throws ManagerBeanException {
		saveResponse();
		updateSurveyResponse( this.nextQuestion );
	}

	public void onFinishSurveyResponse( ActionEvent event ) throws ManagerBeanException {
		saveResponse();
		onInitSurveyResponse(event);
	}

	private void updateResponseValue() throws ManagerBeanException {
		if ( this.questionValueId != null ) {
			IManagerBean bean = BeanManager.getManagerBean(QuestionValue.class);
			QuestionValue questionValue = (QuestionValue) bean.get( this.questionValueId );
			questionValue.copyValues(response);			
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
			if (! getQuestionValues().isEmpty() ) {
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
	
	private void updateSurveyResponse( SurveyQuestion sq ) throws ManagerBeanException {
		this.surveyQuestion = sq;
		refreshQuestionValues( this.surveyQuestion );
		this.nextQuestion = getNextQuestion( this.surveyQuestion );
		this.response = new SurveyResponseDetail();
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
	
	private SurveyQuestion getNextQuestion( SurveyQuestion surveyQuestion ) throws ManagerBeanException {
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
	
	private void resetTarget() {
		this.targetSelected = false;
		this.target = new Target();		
	}
	
	private void initTarget( Target target ) throws ManagerBeanException {
		this.targetSelected = true;
		Integer id = target.getRegistry().getId();
		this.phone = getTargetMedia( id, MediaType.FIXED_PHONE );
		this.cellular = getTargetMedia( id, MediaType.CELLULAR );
		this.fax = getTargetMedia( id, MediaType.FAX );
		this.email = getTargetMedia( id, MediaType.EMAIL );
		this.web = getTargetMedia( id, MediaType.WEB );
		this.mainAddress = getTargetAddress(id);
	}
	
	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		resetTarget();
	}

	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		try {
			Target newTarget = (Target) event.getController().getTo();
			initTarget( newTarget );
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException( e.getMessage(), e );
		}
	}
	
}
