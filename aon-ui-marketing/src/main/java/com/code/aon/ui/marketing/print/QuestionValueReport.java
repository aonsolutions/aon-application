
package com.code.aon.ui.marketing.print;

import java.util.Map;

import org.apache.commons.lang.ObjectUtils;

import com.code.aon.marketing.ActionTarget;
import com.code.aon.marketing.SurveyResponse;
import com.code.aon.marketing.SurveyResponseDetail;
import com.code.aon.registry.Question;
import com.code.aon.registry.enumeration.QuestionType;
import com.code.aon.ui.util.AonUtil;

public class QuestionValueReport {
	
	private SurveyResponseDetail to;
	
	private SurveyResponse master;
	
	private ActionTarget actionTarget;
	
	private Map<Integer,String> responses;
	
	public QuestionValueReport(SurveyResponseDetail to, SurveyResponse master, ActionTarget actionTarget ) {
		this.to = to;
		this.actionTarget = actionTarget;
		this.master = master;
	}
	
	public Map<Integer, String> getResponses() {
		return responses;
	}

	public void setResponses(Map<Integer, String> responses) {
		this.responses = responses;
	}

	public SurveyResponseDetail getTo() {
		return to;
	}
	
	public SurveyResponse getMaster() {
		return master;
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