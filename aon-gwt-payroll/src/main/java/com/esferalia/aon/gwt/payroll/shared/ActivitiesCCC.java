package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ActivitiesCCC implements Serializable{
	
	private Map<Integer, String> activities;
	private Map<Integer, CCCInfo> cccs = new HashMap<>();
	
	public ActivitiesCCC(){
		super();
		this.activities = new HashMap<Integer, String>();
		this.cccs = new HashMap<Integer, CCCInfo>();
	}

	public Map<Integer, String> getActivities() {
		return activities;
	}

	public void setActivities(Map<Integer, String> activities) {
		this.activities = activities;
	}

	public Map<Integer, CCCInfo> getCccs() {
		return cccs;
	}

	public void setCccs(Map<Integer, CCCInfo> cccs) {
		this.cccs = cccs;
	}
	
	public void addCCC(Integer cccId, String ccc, Byte type, String geozoneName, String geozoneCode, Integer activityId) {
		CCCInfo cccInfo = new CCCInfo(ccc, type, geozoneName, geozoneCode, activityId, cccId);
		this.cccs.put(cccId, cccInfo);
	}
}
