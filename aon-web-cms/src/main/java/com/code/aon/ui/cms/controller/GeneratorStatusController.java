package com.code.aon.ui.cms.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.ActionEvent;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.cms.IGeneratorLogger;
import com.code.aon.ui.cms.util.ControllerUtil;
import com.code.aon.ui.cms.util.FTPUtil;

public class GeneratorStatusController implements IGeneratorLogger {
	
	private static final Logger LOGGER = Logger.getLogger(GeneratorStatusController.class.getName());
	
	private static final DateFormat TIME_FORMAT = SimpleDateFormat.getTimeInstance(DateFormat.MEDIUM); 

	private boolean generated;

	private boolean generatedOk;

	private boolean activePoll;

	private boolean published;

	private List<String> status;

	private List<String> errors;

	public GeneratorStatusController() {
		this.status = new ArrayList<String>();
		this.errors = new ArrayList<String>();
	}

	public void onInit(ActionEvent event) {
		onReset(event);
		this.activePoll = true;
	}

	public void onReset(ActionEvent event) {
		this.generated = false;
		this.generatedOk = false;
		this.published = false;
		this.activePoll = false;
		this.status.clear();
		this.errors.clear();
	}
	
	public List<String> getStatus() {
		return this.status;
	}
	
	public int getStatusSize() {
		return this.status.size();
	}

	public List<String> getErrors() {
		return this.errors;
	}
	
	public int getErrorsSize() {
		return this.errors.size();
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
	public void warning(String msg) {
		addErrorMessage(" <i>WARNING: " + msg + "</i>");
	}

	public boolean isActivePoll() {
		return this.activePoll;
	}

	public boolean isCorrect() {
		return this.generatedOk;
	}

	public boolean isGenerated() {
		return this.generated;
	}

	public boolean isPublished() {
		return this.published;
	}

	public void finalized() {
		this.generated = true;
		if (this.errors.size()==0) {
			this.generatedOk = true;
		}
		this.activePoll = false;
	}

	public void onActivePoll(ActionEvent event){
		this.activePoll = true;
	}

	public void onPublish(ActionEvent event) throws ManagerBeanException {
		this.activePoll = true;
		this.status.clear();
		this.errors.clear();
		try {
			if (FTPUtil.uploadFTP())  {
				this.published = true;
			}
		}catch (Throwable th) {		
			LOGGER.log(Level.SEVERE, th.getMessage(), th);
		}finally{
			this.activePoll = false;
		}
	}

	public void onPublishPreview(ActionEvent event) throws ManagerBeanException {
		this.activePoll = true;
		this.status.clear();
		this.errors.clear();
		try {
			if (FTPUtil.uploadPreviewFTP())
				this.published = true;
		}catch (Throwable th) {			
			LOGGER.log(Level.SEVERE, th.getMessage(), th);
		}finally{
			this.activePoll = false;
		}
	}

	public String getPreviewURL() {
		return ControllerUtil.getPreviewURL();
	}

	public String getWebURL() {
		return ControllerUtil.getWebURL();
	}
	
}
