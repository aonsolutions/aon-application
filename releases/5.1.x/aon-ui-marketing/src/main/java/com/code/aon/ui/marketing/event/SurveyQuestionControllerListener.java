package com.code.aon.ui.marketing.event;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.marketing.Survey;
import com.code.aon.marketing.SurveyQuestion;
import com.code.aon.marketing.dao.IMarketingAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.marketing.controller.IMarketingConstants;

public class SurveyQuestionControllerListener extends ControllerAdapter implements IMarketingConstants {

	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		IController controller = event.getController();
		SurveyQuestion surveyQuestion = (SurveyQuestion) controller.getTo();
		surveyQuestion.setPosition(1);
		IController surveyController = FormUtil.getController(SURVEY_CONTROLLER_NAME);
		Survey survey = (Survey) surveyController.getTo();
		try {
			Criteria criteria = new Criteria();
			String surveyId = controller.getFieldName(IMarketingAlias.SURVEY_QUESTION_SURVEY_ID);
			criteria.addEqualExpression( surveyId, survey.getId() );
			String position = controller.getFieldName(IMarketingAlias.SURVEY_QUESTION_POSITION);
			Integer result = (Integer) controller.getManagerBean().getUniqueResult(Projection.max(position), criteria);
			if ( result != null ) {
				surveyQuestion.setPosition( result+1 );
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException( e.getMessage(), e );
		}
	}

}
