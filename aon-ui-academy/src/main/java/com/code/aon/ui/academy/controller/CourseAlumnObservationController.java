package com.code.aon.ui.academy.controller;

import static com.code.aon.ui.academy.controller.IAcademyConstants.EVALUATION_OBSERVATION_CONTROLLER_NAME;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.academy.CourseAlumn;
import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;

public class CourseAlumnObservationController extends LinesController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private static final Logger LOGGER = LoggerFactory.getLogger(CourseAlumnObservationController.class);
	
	private int evaluation;

    public CourseAlumnObservationController() {
    	this.evaluation = 1;
    }
	
	/**
	 * @return the evaluation
	 */
	public int getEvaluation() {
		return evaluation;
	}

	/**
	 * @param evaluation the evaluation to set
	 */
	public void setEvaluation(int evaluation) {
		this.evaluation = evaluation;
	}

	public void onEvaluationChanged(ValueChangeEvent event) {
		setEvaluation( (Integer) event.getNewValue() );
		if ( getTo() != null ) {
			updateEvaluationObservation();
		}
	}
	
	private void updateEvaluationObservation() {
		EvaluationObservationController eoc = (EvaluationObservationController) AonUtil.getRegisteredBean(EVALUATION_OBSERVATION_CONTROLLER_NAME);
		eoc.setEvaluation(this.evaluation);
		eoc.setCourseAlumn( (CourseAlumn) getTo() );
		try {
			eoc.refresh();
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e );
		}		
	}

	@Override
	public void onSelect(ActionEvent event) {
		super.onSelect(event);
		updateEvaluationObservation();
	}
		
}