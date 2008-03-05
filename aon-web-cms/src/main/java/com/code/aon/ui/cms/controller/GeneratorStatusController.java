package com.code.aon.ui.cms.controller;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import javax.faces.event.ActionEvent;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.cms.util.FTPUtil;


public class GeneratorStatusController  {

	private boolean generated = false;
	
	private boolean activePoll = false;
	
	private List<String> status;

	private List<String> errors;

	public void onInit(ActionEvent event){
		generated = false;
		activePoll = true;
		status = new ArrayList<String>();
		errors = new ArrayList<String>();
	}
	
	public List<String> getStatus() {
		return status;
	}
	
	public List<String> getErrors() {
		return errors;
	}
	
	public void addMessage(String msg) {
		status.add(0,GregorianCalendar.getInstance().getTime()+": "+msg);
	}	
	
	public void addErrorMessage(String msg) {
		errors.add(0,GregorianCalendar.getInstance().getTime()+": "+msg);
	}	

	public boolean isActivePoll() {
		return activePoll;
	}

	public boolean isCorrect() {
		return errors.size()==0;
	}

	public boolean isGenerated() {
		return generated;
	}

	public void finalized() {
		this.generated = true;
		this.activePoll = false;
	}

	public void onPublish(ActionEvent event) throws ManagerBeanException {
		FTPUtil.uploadFTP();
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
