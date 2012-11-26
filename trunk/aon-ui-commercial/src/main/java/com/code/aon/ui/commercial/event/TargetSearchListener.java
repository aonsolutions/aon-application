package com.code.aon.ui.commercial.event;


import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import com.code.aon.commercial.CommercialActivity;
import com.code.aon.commercial.Question;
import com.code.aon.commercial.QuestionValue;
import com.code.aon.commercial.enumeration.CommercialTrackingStatus;
import com.code.aon.commercial.enumeration.TargetStatus;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.marketing.ActionTarget;
import com.code.aon.marketing.MarketingAction;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ql.ProjectionList;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.seller.Seller;
import com.code.aon.ui.commercial.controller.CommercialCollectionsController;
import com.code.aon.ui.commercial.controller.ICommercialConstants;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.registry.controller.event.RegistrySearchListener;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class TargetSearchListener extends RegistrySearchListener implements ICommercialConstants {

	private TargetStatus[] targetStatuses;
	
	private Seller seller;
	
	private CommercialActivity activity;
	
	private CommercialTrackingStatus[] trackingStatuses;
	
	private String userName;

	private Question question;
	
	private QuestionValue questionValue;
	
	private List<SelectItem> questionValues;
	
	private Integer questionValueId;
	
	private MarketingAction action;
	
	public MarketingAction getAction() {
		return action;
	}

	public void setAction(MarketingAction action) {
		this.action = action;
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
	
	public void setQuestionValues(List<SelectItem> questionValues) {
		this.questionValues = questionValues;
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
	
	public QuestionValue getQuestionValue() {
		return questionValue;
	}

	public void setQuestionValue(QuestionValue questionValue) {
		this.questionValue = questionValue;
	}

	public TargetStatus[] getTargetStatuses() {
		return targetStatuses;
	}

	public void setTargetStatuses(TargetStatus[] targetStatuses) {
		this.targetStatuses = targetStatuses;
	}
	
	public Seller getSeller() {
		return seller;
	}

	public void setSeller(Seller seller) {
		this.seller = seller;
	}
	
	public CommercialActivity getActivity() {
		return activity;
	}

	public void setActivity(CommercialActivity activity) {
		this.activity = activity;
	}	
	
	public CommercialTrackingStatus[] getTrackingStatuses() {
		return trackingStatuses;
	}

	public void setTrackingStatuses(CommercialTrackingStatus[] trackingStatuses) {
		this.trackingStatuses = trackingStatuses;
	}
	
	public void questionChanged( LookupChangeEvent event ) throws ManagerBeanException {
		Question question = (Question) event.getNewValue();
		if ( event.getNewValue() != null ) {
			QuestionValue qv = new QuestionValue();
			qv.setQuestion(question);
			setQuestionValue( qv );
			questionValues = CommercialCollectionsController.getQuestionValues(question);
		} else {
			resetQuestionValue();
		}
	}
	
	private void resetQuestionValue() {
		setQuestionValue(null);
		setQuestionValueId(null);
		setQuestionValues(null);		
	}
	
	@Deprecated
	public String getUserName() {
		return userName;
	}
	@Deprecated
	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Override
	protected void init() throws ManagerBeanException {
		TargetStatus[] defaultTargetStatus = {TargetStatus.ACTIVE};
		setTargetStatuses(defaultTargetStatus);
		setActivity(null);
		setUserName(null);
		setTrackingStatuses( new CommercialTrackingStatus[0] );
		IManagerBean sellerBean = BeanManager.getManagerBean(Seller.class);
		setSeller( (Seller) sellerBean.createNewTo() );
    	CommercialCollectionsController collections = (CommercialCollectionsController) AonUtil.getRegisteredBean(ICommercialConstants.COLLECTIONS_CONTROLLER_NAME);
		collections.refreshActivities();
		setAction( (MarketingAction) BeanManager.getManagerBean(MarketingAction.class).createNewTo() );
		setQuestion( (Question) BeanManager.getManagerBean(Question.class).createNewTo() );		
		resetQuestionValue();
		super.init();
	}
	
	private void completeCriteria( Criteria criteria, QuestionValue qv ) {
		switch ( qv.getQuestion().getType() ) {
			case BOOLEAN:
			case NUMBER:
				criteria.addEqualExpression("Target.profiles.number", qv.getNumber());
				break;
			case DATE:
				criteria.addEqualExpression("Target.profiles.date", qv.getDate());
				break;
			case TEXT:
				criteria.addEqualExpression("Target.profiles.text", qv.getText());
				break;
		}		
	}
	
	public void addActionSubQuery(MarketingAction action, Criteria criteria) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(ActionTarget.class);			
		Criteria subCriteria = new Criteria();
		subCriteria.addEqualExpression(bean.getFieldName(IEntityAlias.ACTION_TARGET_ACTION_ID), action.getId());
		String idAlias = bean.getFieldName(IEntityAlias.ACTION_TARGET_TARGET_ID);
		ProjectionList pl = new ProjectionList( Projection.property(idAlias) );
		Expression exp = ExpressionUtilities.getSubQueryExpression(ActionTarget.class, subCriteria, pl);
		criteria.addInExpression(getFieldName(IEntityAlias.TARGET_ID), exp);			
	}
	
	
	@Override
	protected void completeCriteria( Criteria criteria ) throws ManagerBeanException, ExpressionException {
		if (!ArrayUtils.isEmpty(getTargetStatuses())) {
			String status = getController().resolveAlias(IEntityAlias.TARGET_STATUS);
			addEnumToCriteria(criteria, status, getTargetStatuses());
		}
		if ( (getSeller() != null) && (getSeller().getId() != null) ) {
			String activity = getController().resolveAlias("Target_trackings_seller_id");
			criteria.addEqualExpression(activity, getSeller().getId());			
		}
		if (getActivity() != null) {
			String activity = getController().resolveAlias("Target_trackings_activity_id");
			criteria.addEqualExpression(activity, getActivity().getId());			
		}		
		if (! ArrayUtils.isEmpty(getTrackingStatuses()) ) {
			String status = getController().resolveAlias("Target_trackings_status");
			addEnumToCriteria( criteria, status, getTrackingStatuses() );
		}
		if ( (getAction() != null) && (getAction().getId() != null) ) {
			addActionSubQuery(getAction(), criteria);			
		}
		
		if ( isQuestionResolved() ) {
			criteria.addEqualExpression("Target.profiles.question.id", getQuestion().getId());	
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
		
		// ?????????
		if (! StringUtils.isEmpty(getUserName()) ){					
			criteria.addEqualExpression("id", getTargetId());
		}
		// ?????????
		
		super.completeCriteria( criteria );
	}
	
	@SuppressWarnings("unchecked")
	@Deprecated
	private Integer getTargetId() throws ManagerBeanException {
//		String select = "select ec.target "
//			+ "from Ectarget as ec "
//			+ "where ec.login='" + getUserName() + "')))";	
//		Session session = HibernateUtil.getSession(HibernateUtil.getSessionFactoryName());
//		Query query = session.createQuery(select);
//		List<Target> targetList = query.list();
//		if (targetList.size() > 0) {
//			return targetList.get(0).getId();
//		}
		return -1;
	}
}