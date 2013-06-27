package com.code.aon.ui.commercial.event;


import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.commons.lang.ArrayUtils;

import com.code.aon.commercial.enumeration.TargetStatus;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.marketing.ActionTarget;
import com.code.aon.marketing.MarketingAction;
import com.code.aon.product.Item;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ql.ProjectionList;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.registry.Question;
import com.code.aon.registry.QuestionValue;
import com.code.aon.seller.Seller;
import com.code.aon.ui.commercial.controller.CommercialCollectionsController;
import com.code.aon.ui.commercial.controller.ICommercialConstants;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.registry.controller.RegistryCollectionsController;
import com.code.aon.ui.registry.controller.event.RegistrySearchListener;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class TargetSearchListener extends RegistrySearchListener implements ICommercialConstants {

	private TargetStatus[] targetStatuses;
	
	private Seller seller;
	
	private Item item;

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
	
	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
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

	@Override
	protected void init() throws ManagerBeanException {
		TargetStatus[] defaultTargetStatus = {TargetStatus.ACTIVE};
		setTargetStatuses(defaultTargetStatus);
		IManagerBean sellerBean = BeanManager.getManagerBean(Seller.class);
		setSeller( (Seller) sellerBean.createNewTo() );
		IManagerBean itemBean = BeanManager.getManagerBean(Item.class);
		setItem( (Item) itemBean.createNewTo() );
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
			String seller = getController().resolveAlias("Target_sellers_seller_id");
			criteria.addEqualExpression(seller, getSeller().getId());			
		}
		if ( (getItem() != null) && (getItem().getId() != null) ) {
			String item = getController().resolveAlias("Target_items_item_id");
			criteria.addEqualExpression(item, getItem().getId());			
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
		super.completeCriteria( criteria );
	}

}