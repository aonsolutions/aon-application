package com.code.aon.ui.academy.controller;

import static com.code.aon.ui.academy.controller.IAcademyConstants.ABSENCE_CONTROLLER_NAME;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.academy.CourseAlumn;
import com.code.aon.common.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;

public class CourseAlumnAbsenceController extends LinesController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private static final Logger LOGGER = LoggerFactory.getLogger(CourseAlumnAbsenceController.class);
	
	private int evaluation;

    public CourseAlumnAbsenceController() {
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
			updateAbsence();
		}
	}
	
	private void updateAbsence() {
		AbsenceController ac = (AbsenceController) AonUtil.getRegisteredBean(ABSENCE_CONTROLLER_NAME);
		ac.setEvaluation(this.evaluation);
		ac.setCourseAlumn( (CourseAlumn) getTo() );
		try {
			ac.refresh();
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e );
		}		
	}

	@Override
	public void onSelect(ActionEvent event) {
		super.onSelect(event);
		updateAbsence();
	}
		
}