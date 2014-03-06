package com.code.aon.ui.academy.controller;

import javax.faces.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.academy.CourseAlumn;
import com.code.aon.academy.EvaluationObservation;
import com.code.aon.academy.Observation;
import com.code.aon.common.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.form.BasicController;
import com.esferalia.aon.entity.IEntityAlias;

public class EvaluationObservationController extends BasicController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private static final Logger LOGGER = LoggerFactory.getLogger(EvaluationObservationController.class);
	
	private int evaluation;
	
	private CourseAlumn courseAlumn;
	
	private Observation observation;

	public int getEvaluation() {
		return evaluation;
	}

	public void setEvaluation(int evaluation) {
		this.evaluation = evaluation;
	}

	public CourseAlumn getCourseAlumn() {
		return courseAlumn;
	}

	public void setCourseAlumn(CourseAlumn courseAlumn) {
		this.courseAlumn = courseAlumn;
	}
	
	public Observation getObservation() {
		return observation;
	}

	public void setObservation(Observation observation) {
		this.observation = observation;
	}

	public void refresh() throws ManagerBeanException {
		clearCriteria();
		Criteria criteria = getCriteria();
		criteria.addEqualExpression(getFieldName(IEntityAlias.EVALUATION_OBSERVATION_EVALUATION), evaluation);
		criteria.addEqualExpression(getFieldName(IEntityAlias.EVALUATION_OBSERVATION_COURSE_ALUMN_ID), courseAlumn.getId());
		initializeModel();
	}
	
	private void resetObservation() {
		try {
			setObservation( (Observation) BeanManager.getManagerBean(Observation.class).createNewTo() );
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e );
		}				
	}
	
	@Override
	public void onReset(ActionEvent event) {
		super.onReset(event);
		EvaluationObservation eo = (EvaluationObservation) getTo();
		eo.setEvaluation(evaluation);
		eo.setCourseAlumn(courseAlumn);
		resetObservation();
	}
	
	@Override
	public void onSelect(ActionEvent event) {
		super.onSelect(event);
		resetObservation();
	}	

	public void onObservationChanged( LookupChangeEvent event ) {
		if ( event.getNewValue() != null ) {
			Observation observation = (Observation) event.getNewValue();
			EvaluationObservation eo = (EvaluationObservation) getTo(); 
			eo.setComments(observation.getDescription());
		}
	}

}
