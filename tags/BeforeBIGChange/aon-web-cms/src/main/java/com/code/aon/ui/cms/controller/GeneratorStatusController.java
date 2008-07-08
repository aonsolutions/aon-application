package com.code.aon.ui.cms.controller;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import javax.faces.event.ActionEvent;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.cms.util.ControllerUtil;
import com.code.aon.ui.cms.util.FTPUtil;


public class GeneratorStatusController  {

	private boolean generated = false;

	private boolean generatedOk = false;

	private boolean activePoll = false;

	private boolean published = false;

	private List<String> status;

	private List<String> errors;

	public void onInit(ActionEvent event){
		this.generated = false;
		this.generatedOk = false;
		this.published = false;
		this.activePoll = true;
		this.status = new ArrayList<String>();
		this.errors = new ArrayList<String>();
	}
	
	public List<String> getStatus() {
		return this.status;
	}
	
	public List<String> getErrors() {
		return this.errors;
	}
	
	public void addMessage(String msg) {
		this.status.add(0,GregorianCalendar.getInstance().getTime()+": "+msg);
	}	
	
	public void addErrorMessage(String msg) {
		this.errors.add(0,GregorianCalendar.getInstance().getTime()+": "+msg);
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
		if (this.errors.size()==0)
			this.generatedOk = true;
		this.activePoll = false;
	}

	public void onActivePoll(ActionEvent event){
		this.activePoll = true;
	}

	public void onPublish(ActionEvent event) throws ManagerBeanException {
		this.activePoll = true;
		this.status = new ArrayList<String>();
		this.errors = new ArrayList<String>();
		try {
			if (FTPUtil.uploadFTP())
				this.published = true;
		}catch (Exception e) {
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
	
	private static String checkMem(String data) {
		long freeMemory = Runtime.getRuntime().freeMemory();
		long totalMemory = Runtime.getRuntime().totalMemory();
		long maxMemory = Runtime.getRuntime().maxMemory();
		long memoryUsed = totalMemory-freeMemory;
		data += "-------------> "+(memoryUsed/(1024*1024))+" of "+(maxMemory/(1024*1024))+" MB used";
		return data;
	}
	
}
