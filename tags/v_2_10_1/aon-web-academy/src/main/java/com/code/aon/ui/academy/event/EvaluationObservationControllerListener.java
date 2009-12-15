package com.code.aon.ui.academy.event;

import com.code.aon.academy.CourseAlumn;
import com.code.aon.academy.EvaluationObservation;
import com.code.aon.ui.academy.controller.CourseAlumnMarkController;
import com.code.aon.ui.academy.controller.EvaluationObservationController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;

public class EvaluationObservationControllerListener extends ControllerAdapter {

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		CourseAlumnMarkController courseAlumnMarkController = (CourseAlumnMarkController)AonUtil.getController("courseAlumnMark");
		EvaluationObservationController evaluationObservationController = (EvaluationObservationController)event.getController();
		EvaluationObservation evaluationObservation = (EvaluationObservation)evaluationObservationController.getTo();
		evaluationObservation.setAlumn((CourseAlumn)courseAlumnMarkController.getTo());
		evaluationObservation.setEvaluation(courseAlumnMarkController.getEvaluation());
	}
	
}
