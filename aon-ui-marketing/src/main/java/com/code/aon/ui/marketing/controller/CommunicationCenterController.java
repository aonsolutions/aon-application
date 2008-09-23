package com.code.aon.ui.marketing.controller;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.ActionEvent;

import com.code.aon.commercial.Target;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.marketing.Survey;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class CommunicationCenterController extends ControllerAdapter {
	
	private static final Logger LOGGER = Logger.getLogger(CommunicationCenterController.class.getName());
	
	private Date date;
	
	private Survey survey;
	
	private Target target;
	
	private boolean targetSelected;
	
	public CommunicationCenterController() {
		this.date = new Date();
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Survey getSurvey() {
		return survey;
	}

	public void setSurvey(Survey survey) {
		this.survey = survey;
	}

	public Target getTarget() {
		return target;
	}

	public void setTarget(Target target) {
		this.target = target;
	}
	
	public boolean isTargetSelected() {
		return targetSelected;
	}

	public void setTargetSelected(boolean targetSelected) {
		this.targetSelected = targetSelected;
	}

	public void onStartSurveyResponse( ActionEvent event ) {
		this.targetSelected = false;
		this.target = new Target();
	}

	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		this.targetSelected = false;
	}

	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		this.targetSelected = true;
	}
	
}
