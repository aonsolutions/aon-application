package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("serial")
public class MainCCCInfo implements Serializable {
	
	private Map<Integer, CCCInfo> cccs;
	private Map<Integer, CCCInfo> deletedCCCs;
	private Map<Integer, String> activities;
	
	public MainCCCInfo() {
		super();
		this.cccs = new HashMap<Integer, CCCInfo>();
		this.deletedCCCs = new HashMap<Integer, CCCInfo>();
		this.activities = new HashMap<Integer, String>();
	}

	public Map<Integer, CCCInfo> getCccs() {
		return cccs;
	}

	public void setCccs(Map<Integer, CCCInfo> cccs) {
		this.cccs = cccs;
	}
	
	public Map<Integer, CCCInfo> getDeletedCCCs() {
		return deletedCCCs;
	}

	public void setDeletedCCCs(Map<Integer, CCCInfo> deletedCCCs) {
		this.deletedCCCs = deletedCCCs;
	}

	public Map<Integer, String> getActivities() {
		return activities;
	}

	public void setActivities(Map<Integer, String> activities) {
		this.activities = activities;
	}

	public void insertCCC(Integer cccId, int activityId, byte cccRegimeType, String cccRegimeCode, String ccc, String province, String provinceCode) {
		Boolean useByContracts = null == this.cccs.get(cccId) ? false : this.cccs.get(cccId).isUseByContracts();
		this.cccs.put(cccId, new CCCInfo(activityId, cccRegimeType, cccRegimeCode, ccc, province, provinceCode, useByContracts));
	}

	public void deleteCCC(Integer cccId) {
		if(cccId > 0) {
			CCCInfo cccInfo = this.cccs.get(cccId);
			this.deletedCCCs.put(cccId, cccInfo);
		}
		this.cccs.remove(cccId);
	}
	
}
