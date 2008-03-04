package com.code.aon.ui.cms.controller;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import javax.faces.event.ActionEvent;


public class GeneratorStatusController  {

	private List<String> status;
	
	public void onInit(ActionEvent event){
		status = new ArrayList<String>();
	}
	
	public List<String> getStatus() {
		return status;
	}
	
	public void addMessage(String msg) {
		status.add(GregorianCalendar.getInstance().getTime()+": "+msg);
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
