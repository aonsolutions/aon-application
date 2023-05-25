package com.code.aon.ui.marketing.controller;

import static com.code.aon.ui.commercial.controller.ICommercialConstants.COMMERCIAL_TRACKING_CONTROLLER_NAME;
import static com.code.aon.ui.groupware.controller.IGroupWareConstants.ALARM_CONTROLLER_NAME;
import static com.code.aon.ui.webmail.controller.IWebMailConstants.BEAN_MESSAGE;

import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import jakarta.mail.Address;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.commercial.CommercialTracking;
import com.code.aon.commercial.ProjectCommercial;
import com.code.aon.commercial.Target;
import com.code.aon.commercial.enumeration.ProjectSource;
import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.User;
import com.code.aon.faces.controller.RichLookupBean;
import com.code.aon.groupware.Alarm;
import com.code.aon.groupware.enumeration.AlarmSource;
import com.code.aon.marketing.ActionTarget;
import com.code.aon.marketing.MarketingAction;
import com.code.aon.marketing.News;
import com.code.aon.marketing.Newsletter;
import com.code.aon.marketing.Survey;
import com.code.aon.marketing.SurveyQuestion;
import com.code.aon.marketing.SurveyResponse;
import com.code.aon.marketing.SurveyResponseDetail;
import com.code.aon.marketing.SurveyWorkflow;
import com.code.aon.marketing.enumeration.ActionMediaType;
import com.code.aon.marketing.enumeration.ActionTargetStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ql.ProjectionList;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.registry.Question;
import com.code.aon.registry.QuestionValue;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryMedia;
import com.code.aon.registry.RegistryProfile;
import com.code.aon.registry.enumeration.AddressType;
import com.code.aon.registry.enumeration.MediaType;
import com.code.aon.registry.enumeration.QuestionType;
import com.code.aon.ui.commercial.controller.CommercialTrackingController;
import com.code.aon.ui.commercial.controller.ICommercialConstants;
import com.code.aon.ui.commercial.controller.ProjectCommercialController;
import com.code.aon.ui.commercial.event.ProjectCommercialSearchListener;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.common.serialize.SerializableListDataModel;
import com.code.aon.ui.company.util.CompanyEmailUtil;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.form.DataScrollerState;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.form.event.IControllerListener;
import com.code.aon.ui.groupware.controller.AlarmController;
import com.code.aon.ui.groupware.controller.IGroupWareConstants;
import com.code.aon.ui.mailing.MailData;
import com.code.aon.ui.mailing.MailingManager;
import com.code.aon.ui.marketing.event.CampaignActionTargetSearchListener;
import com.code.aon.ui.registry.controller.RegistryCollectionsController;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.controller.MessageController;
import com.esferalia.aon.entity.IEntityAlias;

public class CommunicationCenterController extends DataScrollerState implements IMarketingConstants {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private static final Logger LOGGER = LoggerFactory.getLogger(CommunicationCenterController.class.getName());
	
	private Date date;
	
	private MarketingAction action;
	
	private Survey survey;
	
	private News news;
	
	private Newsletter newsletter;
	
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
	
	private boolean newsSelected;
	
	private boolean newsletterSelected;
	
	private int pendingTargets;
	
	private User user;
	
	private int numberOfTargetsInEmail;
	
	private IControllerListener projectCommercialListener;
	
	private String newEmailAction;
	
	private String backAction;

	private String backActionListener;
	
	public CommunicationCenterController() {
		this.date = new Date();
		this.questionValues = new LinkedList<SelectItem>();
		this.user = UserUtils.getInstance().getLoggedUser();
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public MarketingAction getAction() {
		return action;
	}

	public void setAction(MarketingAction action) throws ManagerBeanException {
		if ( action != null ) {
			this.action = action;
		} else {
			IManagerBean maBean = BeanManager.getManagerBean(MarketingAction.class);
			this.action = (MarketingAction) maBean.createNewTo();
		}
		this.actionSelected = this.action.getId()!=null;
	}

	public Survey getSurvey() {
		return survey;
	}

	public void setSurvey(Survey survey) throws ManagerBeanException {
		if ( survey != null ) {
			this.survey = survey;
		} else {
			IManagerBean surveyBean = BeanManager.getManagerBean(Survey.class);
			this.survey = (Survey) surveyBean.createNewTo();
		}
		this.surveySelected = this.survey.getId()!=null;
	}
	
	public News getNews() {
		return news;
	}

	public void setNews(News news) throws ManagerBeanException {
		if ( news != null ) {
			this.news = news;
		} else {
			IManagerBean newsBean = BeanManager.getManagerBean(News.class);
			this.news = (News) newsBean.createNewTo();
		}
		this.newsSelected = this.news.getId()!=null;
	}

	public Newsletter getNewsletter() {
		return newsletter;
	}

	public void setNewsletter(Newsletter newsletter) throws ManagerBeanException {
		if ( newsletter != null ) {
			this.newsletter = newsletter;
		} else {
			IManagerBean bean = BeanManager.getManagerBean(Newsletter.class);
			this.newsletter = (Newsletter) bean.createNewTo();
		}
		this.newsletterSelected = this.newsletter.getId()!=null;
	}

	public Target getTarget() {
		return target;
	}

	public void setTarget(Target target) throws ManagerBeanException {
		if ( target != null ) {
			this.target = target;
		} else {
			IManagerBean targetBean = BeanManager.getManagerBean(Target.class);
			this.target = (Target) targetBean.createNewTo();
		}
		this.targetSelected = this.target.getId()!=null;
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
	
	public boolean isNewsSelected() {
		return newsSelected;
	}
	
	public boolean isNewsletterSelected() {
		return newsletterSelected;
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
					sb.append( ' ' );
				}
				sb.append( mainAddress.getAddress2() );
			}
			if (! StringUtils.isEmpty(mainAddress.getAddress3()) ) {
				if ( sb.length() > 0 ) {
					sb.append( ' ' );
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
		init();
	}

	private void init() {
		try {
			setAction(null);
			setTarget(null);
			setSurvey(null);
			setNews(null);
			setNewsletter(null);
		} catch (ManagerBeanException e) {
			LOGGER.error( e.getMessage(), e );
		}
		setModel(null);
		this.surveyResponse = null;
		setActionTarget(null);
		setPendingTargets(0);
		setNumberOfTargetsInEmail(1);
		resetBackProccess();
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
		if ( this.action!=null && this.action.getId()!=null ) {
			this.surveyResponse.setAction( this.action );
		}
		this.surveyResponse.setSurvey( this.survey );
		this.surveyResponse.setRegistry( this.target.getRegistry() );
		this.surveyResponse.setCreationDate( new Date() );
		this.surveyResponse.setDate( this.date );
		this.surveyResponse.setUser( user );
		IManagerBean surveyResponseBean = BeanManager.getManagerBean(SurveyResponse.class);
		surveyResponseBean.insert( surveyResponse );
		if ( getActionTarget() != null ) {
			getActionTarget().setSurveyResponse(this.surveyResponse);
			updateActionTarget(false);
		}
		updateSurveyQuestion( getFirstSurveyQuestion() );
		this.nextQuestionAction = NAVIGATION_COMMUNICATION_CENTER_RESPONSE;
	}
	
	public void onNextQuestion( ActionEvent event ) throws ManagerBeanException {
		saveResponse();
		SurveyQuestion surveyQuestion = getNextSurveyQuestion();
		if ( surveyQuestion != null ) {
			updateSurveyQuestion( surveyQuestion );			
		} else {
			this.nextQuestionAction = backAction();
			if ( getActionTarget() != null ) {
				getActionTarget().setStatus(ActionTargetStatus.FINISHED);
				updateActionTarget(false);				
			}
			onBackActionListener(event);
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
	
	private void updateAction( MarketingAction action) throws ManagerBeanException {
		setAction(action);
		ActionMediaType type = action.getMediaType();
		if ( type == ActionMediaType.PHONE ) {
			setSurvey( action.getSurvey() );	
		} else {
			setSurvey(null);
		}
		if ( type==ActionMediaType.PHONE || type==ActionMediaType.EMAIL ) {		
			setNews( action.getNews() );
		} else {
			setNews( null );
		}
		if ( action.getMediaType() == ActionMediaType.NEWSLETTER ) {
			setNewsletter( action.getNewsletter() );
		} else {
			setNewsletter(null);
		}
	}

	private void updateAction( MarketingAction action, boolean includeCurrentTarget ) throws ManagerBeanException {
		updateAction(action);
		if ( action.getMediaType() == ActionMediaType.PHONE ) {
			nextActionTarget( includeCurrentTarget );	
		} else {
			refreshPendingTargets();			
		}
	}
	
	private void updateActionTarget( boolean resetUser ) throws ManagerBeanException {
		if ( getActionTarget() != null ) {
			IManagerBean bean = BeanManager.getManagerBean(ActionTarget.class);
			if ( resetUser ) {
				getActionTarget().setUser(null);
			}
			bean.update(getActionTarget());			
		}
	}
	
	private void updateTargetProfile() throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(RegistryProfile.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_PROFILE_REGISTRY_ID), this.target.getId());
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_PROFILE_QUESTION_ID), getQuestion().getId());
		RegistryProfile registryProfile = null;
		List<ITransferObject> list = bean.getList(criteria);
		if (! list.isEmpty() ) {
			registryProfile = (RegistryProfile) list.get(0);
		} else {
			registryProfile = new RegistryProfile();
			registryProfile.setRegistry( this.target.getRegistry() );
			registryProfile.setQuestion( getQuestion() );
		}
		registryProfile.setLastUpdate( new Date() );
		this.response.copyValues(registryProfile);
		bean.insertOrUpdate( registryProfile );
	}
	
	private void saveResponse() throws ManagerBeanException {	
		if ( getQuestion().getType() != QuestionType.INFO ) {
			if (this.questionValueId != null ) {
				updateResponseValue();
			}
			this.response.setSurveyResponse( this.surveyResponse );
			IManagerBean bean = BeanManager.getManagerBean(SurveyResponseDetail.class);		
			bean.insert( this.response );
			if (! this.response.isNotFilled() ) {
				updateTargetProfile();
			}
		}
	}
	
	private void refreshQuestionValues( SurveyQuestion sq ) throws ManagerBeanException {
		questionValues = RegistryCollectionsController.getQuestionValues(sq.getQuestion());
		this.questionValueId = null;
	}
	
	private void updateSurveyQuestion( SurveyQuestion sq ) throws ManagerBeanException {
		this.surveyQuestion = sq;
		this.response = new SurveyResponseDetail();
		this.response.setQuestion(sq.getQuestion());
		refreshQuestionValues( this.surveyQuestion );
	}

	public boolean isEmptySurvey() throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(SurveyQuestion.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression( bean.getFieldName(IEntityAlias.SURVEY_QUESTION_SURVEY_ID), this.survey.getId() );
		criteria.addNotEqualExpression("SurveyQuestion.question.type", QuestionType.INFO );
		return bean.getCount(criteria) == 0;
	}
	
	private SurveyQuestion getFirstSurveyQuestion() throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(SurveyQuestion.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression( bean.getFieldName(IEntityAlias.SURVEY_QUESTION_SURVEY_ID), this.survey.getId() );
		criteria.addOrder( bean.getFieldName(IEntityAlias.SURVEY_QUESTION_POSITION) );
		List<ITransferObject> list = bean.getList(criteria, 0, 1);
		if (! list.isEmpty() ) {
			return (SurveyQuestion) list.get(0);
		}
		return null;
	}

	private SurveyWorkflow getSurveyWorkflow( SurveyQuestion surveyQuestion ) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(SurveyWorkflow.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression( bean.getFieldName(IEntityAlias.SURVEY_WORKFLOW_SURVEY_QUESTION_ID), surveyQuestion.getId() );
		if (this.questionValueId != null ) {
			criteria.addEqualExpression( bean.getFieldName(IEntityAlias.SURVEY_WORKFLOW_QUESTION_VALUE_ID), this.questionValueId );
		} else {
			String textField = bean.getFieldName(IEntityAlias.SURVEY_WORKFLOW_TEXT);
			if (this.response.getText() != null) {
				criteria.addEqualExpression( textField, this.response.getDate() );
			} else {
				criteria.addNullExpression(textField);
			}
			String dateField = bean.getFieldName(IEntityAlias.SURVEY_WORKFLOW_DATE);
			if (this.response.getDate() != null) {
				criteria.addEqualExpression( dateField, this.response.getDate() );			
			} else {
				criteria.addNullExpression(dateField);
			}
			String numberField = bean.getFieldName(IEntityAlias.SURVEY_WORKFLOW_NUMBER);
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
		criteria.addEqualExpression( bean.getFieldName(IEntityAlias.SURVEY_QUESTION_SURVEY_ID), this.survey.getId() );
		String position = bean.getFieldName(IEntityAlias.SURVEY_QUESTION_POSITION);
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
		criteria.addEqualExpression(mediaBean.getFieldName(IEntityAlias.REGISTRY_MEDIA_REGISTRY_ID), id);
		criteria.addEqualExpression(mediaBean.getFieldName(IEntityAlias.REGISTRY_MEDIA_MEDIA_TYPE), type);
		List<ITransferObject> list = mediaBean.getList(criteria);
		if (! list.isEmpty() ) {
			return (RegistryMedia) list.get(0);
		}
		return null;
	}
	
	private RegistryAddress getTargetAddress( Integer id ) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(RegistryAddress.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_ADDRESS_REGISTRY_ID), id);
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_ADDRESS_ADDRESS_TYPE), AddressType.MAIN);
		List<ITransferObject> list = bean.getList(criteria);
		if (! list.isEmpty() ) {
			return (RegistryAddress) list.get(0);
		}
		return null;
	}	
	
	private void initTarget( Target target ) throws ManagerBeanException {
		Integer id = target.getRegistry().getId();
		this.phone = getTargetMedia( id, MediaType.FIXED_PHONE );
		this.cellular = getTargetMedia( id, MediaType.CELLULAR );
		this.fax = getTargetMedia( id, MediaType.FAX );
		this.email = getTargetMedia( id, MediaType.EMAIL );
		this.web = getTargetMedia( id, MediaType.WEB );
		this.mainAddress = getTargetAddress(id);
	}
	
	private void resetUserActionTargets() throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(ActionTarget.class);
		Criteria criteria = new Criteria();
		String userId = bean.getFieldName(IEntityAlias.ACTION_TARGET_USER_ID);
		criteria.addEqualExpression(userId, user.getId() );
		String actionId = bean.getFieldName(IEntityAlias.ACTION_TARGET_ACTION_ID);
		criteria.addEqualExpression(actionId, this.action.getId());		
		for( ITransferObject to : bean.getList(criteria) ) {
			ActionTarget at = (ActionTarget) to;
			at.setUser(null);
			bean.update(at);
		}		
	}
	
	private void blockActionTarget() throws ManagerBeanException {
		resetUserActionTargets();
		getActionTarget().setUser(user);
		updateActionTarget(false);
	}
	
	public void onNextActionTarget( ActionEvent event ) throws ManagerBeanException {
		resetBackProccess();
		updateActionTarget(true);
		nextActionTarget(false);
	}
	
	private void nextActionTarget( boolean includeCurrentTarget ) throws ManagerBeanException {
		ActionTarget target = getNextActionTarget(includeCurrentTarget);
		if ( target != null ) {
			setActionTarget( target );
			setTarget( getActionTarget().getTarget() );
			initTarget(this.target);
			blockActionTarget();
			refreshPendingTargets();
		} else {
			init();
		}		
	}
	
	public void onUpdateActionTarget( ActionEvent event ) throws ManagerBeanException {
		updateActionTarget(false);
	}

	public void onActionLookupChange(LookupChangeEvent event) {
		init();
		this.actionSelected = event.getNewValue()!=null;	
		if (this.actionSelected) {
			MarketingAction action = (MarketingAction) event.getNewValue();
			try {				
				updateAction(action, false);
			} catch (ManagerBeanException e) {
				AonUtil.addErrorMessage(e.getMessage());
				throw new AbortProcessingException(e);
			}
		}
	}

	private Criteria getPendingTargetsCriteria( IManagerBean bean, boolean onlyCount, boolean includeCurrentTarget, boolean onlyPending ) throws ManagerBeanException {
		Criteria criteria = new Criteria();
		String id = bean.getFieldName(IEntityAlias.ACTION_TARGET_ID);
		if ( !onlyCount ) {
			criteria.addOrder( id );
			Expression exp1 = ExpressionUtilities.getNullExpression("ActionTarget.user");
			Expression exp2 = ExpressionUtilities.getEqualExpression("ActionTarget.user<id", user.getId());
			criteria.addExpression(ExpressionUtilities.getOrExpression(exp1, exp2));		
			if ( getActionTarget() != null ) {
				if ( includeCurrentTarget ) {
					criteria.addGreaterThanOrEqualExpression( id, getActionTarget().getId() );
				} else {
					criteria.addGreaterThanExpression( id, getActionTarget().getId() );	
				}
			}	
		}
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.ACTION_TARGET_ACTION_ID), this.action.getId());
		String status = bean.getFieldName(IEntityAlias.ACTION_TARGET_STATUS);
		if ( onlyPending ) {
			criteria.addEqualExpression(status, ActionTargetStatus.PENDING);
		} else {
			Expression expression1 = ExpressionUtilities.getNotEqualExpression(status, ActionTargetStatus.FINISHED);
			criteria.addExpression(expression1);
			Expression expression2 = ExpressionUtilities.getNotEqualExpression(status, ActionTargetStatus.SENT);
			criteria.addExpression(expression2);
		}
		return criteria;
	}
	
	private ActionTarget getNextActionTarget( boolean includeCurrentTarget ) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(ActionTarget.class);
		Criteria criteria = getPendingTargetsCriteria(bean, false, includeCurrentTarget, true);
		List<ITransferObject> list = bean.getList(criteria, 0, 1);
		if (! list.isEmpty() ) {
			return (ActionTarget) list.get(0);
		}
		criteria = getPendingTargetsCriteria(bean, false, includeCurrentTarget, false);
		list = bean.getList(criteria, 0, 1);
		if (! list.isEmpty() ) {
			return (ActionTarget) list.get(0);
		}
		return null;
	}

	@SuppressWarnings({ "unchecked" })
	public List<Integer> getActionTargets( boolean registryId ) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(ActionTarget.class);
		Criteria criteria = getPendingTargetsCriteria(bean);
		String id = null;
		if ( registryId ) {
			id = bean.getFieldName(IEntityAlias.ACTION_TARGET_TARGET_ID);
		} else {
			id = bean.getFieldName(IEntityAlias.ACTION_TARGET_ID);
		}
		ProjectionList projectList = new ProjectionList(Projection.property(id));
		return bean.getList(projectList, criteria);
	}

	public Criteria getPendingTargetsCriteria( IManagerBean bean ) throws ManagerBeanException {
		return getPendingTargetsCriteria(bean, true, false, false);
	}	
	
	private void refreshPendingTargets() throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(ActionTarget.class);
		Criteria criteria = getPendingTargetsCriteria(bean, true, false, false);
		setPendingTargets(bean.getCount(criteria));
	}
	
	public void onGenerateTargetMailing(ActionEvent event) throws ManagerBeanException {
        List<Integer> targets = getActionTargets(true);
        List<MailData> data = MailingManager.generateMailingList(targets);
        setModel( new SerializableListDataModel(data) );
        List<Integer> actionTargets = getActionTargets(false);
        CampaignActionTargetController.resetStatuses(actionTargets, ActionTargetStatus.FINISHED);
	}		

	@SuppressWarnings("unchecked")
	public void onDownloadMailing(ActionEvent event) throws IOException {
        List<MailData> data = (List<MailData>) getDirectModel().getWrappedData();
        MailingManager.generateMailing(data);
	}		
	
	public void onFinishSurvey(ActionEvent event) throws ManagerBeanException {
		updateActionTarget(false);
		onBackActionListener(event);		
	}

	public void onMarketingActionBackActionListener( ActionEvent event ) {
		try {				
			IController controller = FormUtil.getController(CAMPAIGN_ACTION_CONTROLLER_NAME);
			updateAction((MarketingAction) controller.getTo(), true);
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
		}		
	}

	public void onStartActionTarget( ActionEvent event ) {
		resetBackProccess();
		FacesContext context = FacesContext.getCurrentInstance();
		String idValue = context.getExternalContext().getRequestParameterMap().get("actionTargetId");
		try {				
			ActionTarget at = null;
			if (! StringUtils.isEmpty(idValue) ) {
				IManagerBean bean = BeanManager.getManagerBean(ActionTarget.class);
				Integer id = Integer.valueOf(idValue);
				at = (ActionTarget) bean.get(id);
			}
			if ( at != null ) {
				startActionTarget(at);
			}
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
		}       
		setBackAction(IMarketingConstants.CAMPAIGN_ACTION_CONTROLLER_NAME + IController.FORM_SUFFIX);
		setBackActionListener(IMarketingConstants.COMMUNICATION_CENTER_CONTROLLER_NAME + ".onBackActionTarget");
	}
	
	public void onBackActionTarget( ActionEvent event ) throws ManagerBeanException, ControllerListenerException {
		resetUserActionTargets();
		CampaignActionTargetSearchListener catsl = (CampaignActionTargetSearchListener)
				AonUtil.getRegisteredBean(IMarketingConstants.CAMPAIGN_ACTION_TARGET_SEARCH);
		catsl.onFilter(event);
	}

	public void onGoToActionTarget( ActionEvent event ) throws ManagerBeanException {
		resetBackProccess();
		IController controller = FormUtil.getController(ALARM_CONTROLLER_NAME);
		Alarm alarm = (Alarm) controller.getTo();
		try {				
			IManagerBean bean = BeanManager.getManagerBean(ActionTarget.class);
			ActionTarget at = (ActionTarget) bean.get(alarm.getSourceId());
			if ( at != null ) {
				startActionTarget(at);	
			}
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
		}       
		setBackAction(IGroupWareConstants.ALARM_CONTROLLER_NAME + IController.FORM_SUFFIX);
	}	
	
	public void startActionTarget( ActionTarget at ) throws ManagerBeanException {
		if ( at.getStatus() == ActionTargetStatus.FINISHED ) {
			at.setStatus(ActionTargetStatus.PENDING);	
		}
		setActionTarget( at );			
		updateActionTarget(true);
		updateAction( at.getAction() );
		nextActionTarget(true);
	}	
	
	public void onNewAlarm( ActionEvent event ) {
		AlarmController controller = (AlarmController) AonUtil.getRegisteredBean(ALARM_CONTROLLER_NAME);
		controller.setShowNewAlarmWindow(true);
		controller.onReset(event);
		Alarm alarm = (Alarm) controller.getTo();
		alarm.setSource(AlarmSource.OFFICE);
		alarm.setSourceId(getActionTarget().getId());
	}

	public void onNewEmail( ActionEvent event ) {
		this.newEmailAction = NAVIGATION_COMMUNICATION_CENTER;
		MessageController controller = (MessageController) AonUtil.getRegisteredBean(BEAN_MESSAGE);
		controller.onPrepareEmailWindow(event);
		if ( controller.isShowNewMessageWindow() ) {
			controller.onNewMessage(event);	
			controller.setShowTemplates(false);
			controller.setAppendSignature(false);
			controller.setSaveSent(false);
			if ( isNewsSelected() ) {
				NewsController.initController(controller, getNews());
			} else if ( isNewsletterSelected() ) {
				NewsletterController.initController(controller, getNewsletter());
			}
			controller.setShowNewMessageWindow(false);
			this.newEmailAction = NAVIGATION_COMMUNICATION_CENTER_EMAIL;
		}
	}

	public String newEmailAction() {
		return newEmailAction; 
	}
	
	public void onInitEmail( ActionEvent event ) {
		MessageController controller = (MessageController) AonUtil.getRegisteredBean(BEAN_MESSAGE);
		controller.onPrepareEmailWindow(event);
		if ( controller.isShowNewMessageWindow() ) {
			try {		
				controller.onNewMessage(event);
				if ( isNewsSelected() ) {
					NewsController.initController(controller, getNews());	
				}
				if ( isTargetSelected() ) {
					String[] emails = CompanyEmailUtil.getCommercialEmails(getTarget().getRegistry());
					CompanyEmailUtil.initMessageController(controller, emails);
				}
			} catch (Throwable th) {
				LOGGER.error(th.getMessage(), th);
				AonUtil.addErrorMessage(th.getMessage());
				throw new AbortProcessingException(th.getMessage(), th);
			}		
		}
	}
	
	public void onSendEmail( ActionEvent event ) {
		MessageController controller = (MessageController) AonUtil.getRegisteredBean(BEAN_MESSAGE);
		controller.onSend(event);
		try {
			List<Address> addressList = controller.getSentAddressList();
			String[] emails = CompanyEmailUtil.getEmails(getTarget().getRegistry());
			if (! ArrayUtils.isEmpty(emails) ) {
				for( String email : emails ) {
					try {
						InternetAddress[] addresses = InternetAddress.parse(email, true);
						if (! ArrayUtils.isEmpty(addresses) ) { 
							for( InternetAddress address : addresses ) {
								addressList.remove(address);
							}
						}
					} catch (AddressException e) {
						LOGGER.error( "Error decoding email: " + email, e );
					}
				}
			}
			if (! addressList.isEmpty() ) {
				IManagerBean bean = BeanManager.getManagerBean(RegistryMedia.class);
				for( Address address : addressList ) {
					RegistryMedia rm = new RegistryMedia();
					rm.setMediaType(MediaType.EMAIL);
					rm.setRegistry(getActionTarget().getRegistry());
					rm.setValue(address.toString());
					rm.setAdministrative(true);
					rm.setCommercial(true);
					rm.setTechnical(true);
					bean.insert(rm);
				}				
			}
		} catch (ManagerBeanException e) {
			LOGGER.error( "Error getting target addresses", e );
		}
	}

	public void onTargetBackActionListener( ActionEvent event ) throws ManagerBeanException {
		IController controller = FormUtil.getController(ICommercialConstants.TARGET_CONTROLLER_NAME);
		Target _target = (Target) controller.getTo();
		setTarget( _target );
		initTarget( _target );
	}
	
	public void onSurveyBackActionListener( ActionEvent event ) throws ManagerBeanException {
		IController controller = FormUtil.getController(SURVEY_CONTROLLER_NAME);
		setSurvey( (Survey) controller.getTo() );
	}

	public void onNewsBackActionListener( ActionEvent event ) throws ManagerBeanException {
		IController controller = FormUtil.getController(NEWS_CONTROLLER_NAME);
		setNews( (News) controller.getTo() );
	}

	public void onNewsletterBackActionListener( ActionEvent event ) throws ManagerBeanException {
		IController controller = FormUtil.getController(NEWSLETTER_CONTROLLER_NAME);
		setNewsletter( (Newsletter) controller.getTo() );
	}
	
	public int getNumberOfTargetsInEmail() {
		return numberOfTargetsInEmail;
	}

	public void setNumberOfTargetsInEmail(int numberOfTargetsInEmail) {
		this.numberOfTargetsInEmail = numberOfTargetsInEmail;
	}

	public void onStartSurveyFromProject( ActionEvent event ) throws ManagerBeanException {
		CommercialTrackingController ctc = (CommercialTrackingController) AonUtil.getRegisteredBean(COMMERCIAL_TRACKING_CONTROLLER_NAME);
		CommercialTracking ct = (CommercialTracking) ctc.getTo();
		setDate(new Date());
		setSurvey(ct.getActivity().getSurvey());
		setTarget(ct.getProject().getTarget());
		setAction(null);
		setActionTarget(null);
		setBackAction(ctc.getSurveyReturnAction());
		onStartSurveyResponse(event);
	}

	public void onNewCommercialTracking( ActionEvent event ) throws ManagerBeanException {
		ProjectCommercialController pcc = (ProjectCommercialController) AonUtil.getRegisteredBean(ICommercialConstants.PROJECT_COMMERCIAL_CONTROLLER_NAME);
		pcc.setShowNewTrackingWindow(true);
		CommercialTrackingController ctc = (CommercialTrackingController) AonUtil.getRegisteredBean(COMMERCIAL_TRACKING_CONTROLLER_NAME);
		ctc.onReset(event);
	}

	public IControllerListener getProjectCommercialListener() {
		if ( this.projectCommercialListener == null ) {
			this.projectCommercialListener = new ProjectCommercialFilter();
		}
		return this.projectCommercialListener;
	}
	
	public String backAction() {
		return (backAction != null) ? backAction : NAVIGATION_COMMUNICATION_CENTER;
	}	

	public String getBackAction() {
		return backAction;
	}

	public void setBackAction(String backAction) {
		this.backAction = StringUtils.trimToNull(backAction);
	}

	public String getBackActionListener() {
		return backActionListener;
	}

	public void setBackActionListener(String expression) {
		this.backActionListener = StringUtils.trimToNull(expression);
	}	
	
	public void onBackActionListener(ActionEvent event) {
		if (!StringUtils.isEmpty(this.backActionListener) ) {
			String expression = "#{" + this.backActionListener + "}";
			AonUtil.actionListener(expression, event);
		}
	}	
	
	private void resetBackProccess() {
		setBackAction(null);
		setBackActionListener(null);
	}
	
	private static class ProjectCommercialFilter extends ControllerAdapter {

		private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
		
		public Target getTarget() {
			CommunicationCenterController ccc = (CommunicationCenterController) AonUtil.getRegisteredBean(COMMUNICATION_CENTER_CONTROLLER_NAME);
			return ccc.getTarget();
		}
		
		@Override
		public void afterEditSearch(ControllerEvent event) throws ControllerListenerException {
			ProjectCommercialSearchListener pcsl = (ProjectCommercialSearchListener) AonUtil.getRegisteredBean(ICommercialConstants.PROJECT_COMMERCIAL_SEARCH_CONTROLLER_NAME);
			pcsl.setTarget(getTarget());
		}

		@Override
		public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
			RichLookupBean lookup = (RichLookupBean) AonUtil.getRegisteredBean(ICommercialConstants.PROJECT_COMMERCIAL_LOOKUP_NAME);
			ProjectCommercial pc = (ProjectCommercial) lookup.getTo();
			pc.setStatusDate(new Date());
			pc.setSource(ProjectSource.CALL_CENTER);
			pc.setTarget(getTarget());
		}

	}
	
}
