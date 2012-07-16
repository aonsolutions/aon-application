
package com.code.aon.ui.marketing.print;

import java.util.List;

import org.apache.commons.lang.ObjectUtils;

import com.code.aon.commercial.Question;
import com.code.aon.commercial.enumeration.QuestionType;
import com.code.aon.marketing.ActionTarget;
import com.code.aon.marketing.SurveyResponse;
import com.code.aon.marketing.SurveyResponseDetail;
import com.code.aon.ui.util.AonUtil;

public class QuestionValueReport {
	
	private SurveyResponseDetail to;
	
	private ActionTarget actionTarget;
	
	private List<MarketingQuestionValue> questionList;
	
	public QuestionValueReport(SurveyResponseDetail to, ActionTarget actionTarget ) {
		this.to = to;
		this.actionTarget = actionTarget;
	}
	
	public List<MarketingQuestionValue> getQuestionList() {
		return questionList;
	}

	public void setQuestionList(List<MarketingQuestionValue> questionList) {
		this.questionList = questionList;
	}

	public SurveyResponseDetail getTo() {
		return to;
	}
	
	public Question getQuestion() {
		return to.getQuestion();
	}

	public SurveyResponse getSurveyResponse() {
		return to.getSurveyResponse();
	}
	
	public String getValue() {
		QuestionType type = to.getQuestion().getType();
		Object object = to.getValue(type);
		return ObjectUtils.toString(object);
	}

	public ActionTarget getActionTarget() {
		return actionTarget;
	}
	
	public String getStatus() {
		return actionTarget.getStatus().getName(AonUtil.getCurrentLocale());
	}
	
	public String getCreationDate() {
		return getSurveyResponse().getCreationDate().toString();
	}

}