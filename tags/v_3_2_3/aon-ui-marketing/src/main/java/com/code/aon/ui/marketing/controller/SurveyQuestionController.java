package com.code.aon.ui.marketing.controller;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.marketing.QuestionValue;
import com.code.aon.marketing.SurveyQuestion;
import com.code.aon.marketing.dao.IMarketingAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.form.BasicController;

/**
 * Controller used in the offer maintenance.
 */
public class SurveyQuestionController extends BasicController implements IMarketingConstants {
	
	private boolean showSurveyWorkflowWindow;
	
	private List<SelectItem> questions;
	
	private List<SelectItem> questionValues;

	public List<SelectItem> getQuestions() {
		return questions;
	}

	@SuppressWarnings("unchecked")
	public void refreshQuestions( SurveyQuestion sq ) throws ManagerBeanException {
		questions = new LinkedList<SelectItem>();
		IManagerBean bean = getManagerBean();
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IMarketingAlias.SURVEY_QUESTION_SURVEY_ID), sq.getSurvey().getId());
		Expression expression = ExpressionUtilities.getNotEqualExpression(bean.getFieldName(IMarketingAlias.SURVEY_QUESTION_ID), sq.getId());
		criteria.addExpression( expression );
		criteria.addOrder(bean.getFieldName(IMarketingAlias.SURVEY_QUESTION_POSITION));
		Iterator<ITransferObject> iter = bean.getList(criteria).iterator();
		while(iter.hasNext()){
			SurveyQuestion question = (SurveyQuestion) iter.next();
			String label = question.getPosition() + " - " + StringUtils.abbreviate(question.getQuestion().getText(), 40);
			SelectItem item = new SelectItem(question.getId(), label);
			questions.add(item);
		}
	}	
	
	public List<SelectItem> getQuestionValues() {
		return questionValues;
	}

	private void refreshQuestionValues( SurveyQuestion sq ) throws ManagerBeanException {
		questionValues = new LinkedList<SelectItem>();
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
	}
	
	public boolean isShowSurveyWorkflowWindow() {
		return showSurveyWorkflowWindow;
	}

	public void setShowSurveyWorkflowWindow(boolean showSurveyWorkflowWindow) {
		this.showSurveyWorkflowWindow = showSurveyWorkflowWindow;
	}

	public void onShowSurveyWorflow( ActionEvent event ) throws ManagerBeanException {
		if ( getModel().isRowAvailable() ) {
			onSelect(event);
		}
		SurveyQuestion sq = (SurveyQuestion) getTo();
		refreshQuestions( sq );
		refreshQuestionValues( sq );
		setShowSurveyWorkflowWindow(true);
	}
	
}