package com.code.aon.ui.marketing.event;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.marketing.Question;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.commercial.event.TargetSearchListener;

public class MarketingTargetSearchListener extends TargetSearchListener {
	
	private Question question;

	private String questionText;
	
	public Question getQuestion() {
		return question;
	}

	public void setQuestion(Question question) {
		this.question = question;
	}
	
	public String getQuestionText() {
		return questionText;
	}

	public void setQuestionText(String questionText) {
		this.questionText = questionText;
	}
	
	@Override
	protected void init() throws ManagerBeanException {
		super.init();
		setQuestion( new Question() );
		setQuestionText(null);
	}

	@Override
	protected void completeCriteria() throws ManagerBeanException, ExpressionException {
		super.completeCriteria();
		Criteria criteria = getController().getCriteria();
		if ( (getQuestion() != null) && (getQuestion().getId() != null) ) {
			criteria.addEqualExpression("MarketingTarget.profiles.question.id", getQuestion().getId());			
		}
		if (! StringUtils.isEmpty(getQuestionText()) ) {
			Expression expText = ExpressionUtilities.getExpression(getQuestionText(), "MarketingTarget.profiles.text");
			Expression expNumber = null;
			try {
				expNumber = ExpressionUtilities.getExpression(getQuestionText(), "MarketingTarget.profiles.number");
			} catch (ExpressionException ee ) {
				criteria.addExpression( expText );
			}
			criteria.addExpression( ExpressionUtilities.getOrExpression(expText, expNumber) );
		}
		addEnumToCriteria( criteria, "MarketingTarget.target.registry.medias.mediaType", getMediaTypes().toArray() );
	}

}