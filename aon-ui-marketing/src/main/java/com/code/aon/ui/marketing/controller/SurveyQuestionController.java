package com.code.aon.ui.marketing.controller;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.marketing.SurveyQuestion;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.registry.controller.RegistryCollectionsController;
import com.esferalia.aon.entity.IEntityAlias;

/**
 * Controller used in the offer maintenance.
 */
public class SurveyQuestionController extends BasicController implements IMarketingConstants {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private boolean showSurveyWorkflowWindow;
	
	private List<SelectItem> questions;
	
	private List<SelectItem> questionValues;

	public List<SelectItem> getQuestions() {
		return questions;
	}

	public void refreshQuestions( SurveyQuestion sq ) throws ManagerBeanException {
		questions = new LinkedList<SelectItem>();
		IManagerBean bean = getManagerBean();
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.SURVEY_QUESTION_SURVEY_ID), sq.getSurvey().getId());
		Expression expression = ExpressionUtilities.getNotEqualExpression(bean.getFieldName(IEntityAlias.SURVEY_QUESTION_ID), sq.getId());
		criteria.addExpression( expression );
		criteria.addOrder(bean.getFieldName(IEntityAlias.SURVEY_QUESTION_POSITION));
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
		questionValues = RegistryCollectionsController.getQuestionValues(sq.getQuestion());
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