package com.code.aon.faces.controller;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.event.ActionEvent;

import com.code.aon.common.AonVersion;
import com.code.aon.common.ILogger;
import com.code.aon.ui.util.AonUtil;

public class LogPanelController implements ILogger, Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final DateFormat TIME_FORMAT = SimpleDateFormat.getTimeInstance(DateFormat.MEDIUM); 

	private boolean activePoll;

	private List<String> status;

	private List<String> errors;

	public LogPanelController() {
		this.status = new ArrayList<String>();
		this.errors = new ArrayList<String>();
	}
	
	public static LogPanelController getInstance() {
		return (LogPanelController) AonUtil.getRegisteredBean(IRichConstants.LOG_PANEL_CONTROLLER_NAME);
	}

	public List<String> getStatus() {
		return this.status;
	}
	
	public List<String> getErrors() {
		return this.errors;
	}
	
	private void addMessage(String msg) {
		String time = TIME_FORMAT.format(new Date());
		this.status.add(0,time+" "+msg);
	}	
	
	private void addErrorMessage(String msg) {
		String time = TIME_FORMAT.format(new Date());
		this.errors.add(0, time+" "+msg);
	}	

	@Override
	public void info(String msg) {
		addMessage(" INFO: " + msg);
	}
	
	@Override
	public void error(String msg) {
		addErrorMessage(" <b>ERROR: " + msg + "</b>");
	}

	@Override
	public void warn(String msg) {
		addErrorMessage(" <i>WARNING: " + msg + "</i>");
	}

	public void reset() {
		this.status.clear();
		this.errors.clear();				
	}
	
	public void finish() {
		this.activePoll = false;
	}	
	
	public boolean isActivePoll() {
		return this.activePoll;
	}

	public void onShowWindow(ActionEvent event){
		this.activePoll = true;
		reset();
	}
	
	public void onCloseWindow(ActionEvent event){
		this.activePoll = false;
		reset();
	}	

	public void onDoNothig(ActionEvent event){
	}

}
