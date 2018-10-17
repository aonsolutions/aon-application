package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;

public class CCCInfo implements Serializable{
	private String ccc;
	private Byte type;
	private String geozone;
	private Integer activityId;
	
	public CCCInfo(){
		super();
	}
	
	public CCCInfo(String ccc, Byte type, String geozone, Integer activityId) {
		super();
		this.ccc = ccc;
		this.type = type;
		this.geozone = geozone;
		this.activityId = activityId;
	}

	public String getCcc() {
		return ccc;
	}

	public void setCcc(String ccc) {
		this.ccc = ccc;
	}

	public Byte getType() {
		return type;
	}

	public void setType(Byte type) {
		this.type = type;
	}

	public String getGeozone() {
		return geozone;
	}

	public void setGeozone(String geozone) {
		this.geozone = geozone;
	}

	public Integer getActivityId() {
		return activityId;
	}

	public void setActivityId(Integer activityId) {
		this.activityId = activityId;
	}

}