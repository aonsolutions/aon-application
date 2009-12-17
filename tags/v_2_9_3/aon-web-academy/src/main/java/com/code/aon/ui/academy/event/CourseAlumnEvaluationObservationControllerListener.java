package com.code.aon.ui.academy.event;

import com.code.aon.academy.CourseAlumn;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.academy.controller.CourseAlumnMarkController;
import com.code.aon.ui.academy.controller.EvaluationObservationController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;

public class CourseAlumnEvaluationObservationControllerListener extends ControllerAdapter {

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		try{
			EvaluationObservationController evaluationObservationController = (EvaluationObservationController)AonUtil.getController("evaluation_observation");
			evaluationObservationController.updateCriteria((CourseAlumn)event.getController().getTo(), ((CourseAlumnMarkController)event.getController()).getEvaluation());
			evaluationObservationController.onSearch(null);
		}catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}
}
