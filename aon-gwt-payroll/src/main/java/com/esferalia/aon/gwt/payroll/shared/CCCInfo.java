package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;

public class CCCInfo implements Serializable{
	
	private String ccc;
	private String cccRegimeCode;
	private String cccAccount;
	private Byte type;
	private String geozone;
	private Integer activityId;
	private Integer cccId;
	private Boolean useByContracts;
	
	public CCCInfo(){
		super();
	}
	
	public CCCInfo(String ccc, Byte type, String geozone, Integer activityId, Integer cccId) {
		super();
		this.ccc = ccc;
		this.type = type;
		this.geozone = geozone;
		this.activityId = activityId;
		this.cccId = cccId;
	}
	
	public CCCInfo(String ccc, String cccRegime, String cccAccount, Byte type, String geozone, Integer activityId, Integer cccId, Boolean useByContracts) {
		super();
		this.ccc = ccc;
		this.cccRegimeCode = cccRegime;
		this.cccAccount = cccAccount;
		this.type = type;
		this.geozone = geozone;
		this.activityId = activityId;
		this.cccId = cccId;
		this.useByContracts = useByContracts;
	}

	public String getCcc() {
		return ccc;
	}

	public void setCcc(String ccc) {
		this.ccc = ccc;
	}

	public String getCccRegimeCode() {
		return cccRegimeCode;
	}

	public void setCccRegimeCode(String cccRegimeCode) {
		this.cccRegimeCode = cccRegimeCode;
	}

	public String getCccAccount() {
		return cccAccount;
	}

	public void setCccAccount(String cccAccount) {
		this.cccAccount = cccAccount;
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
	
	public Integer getCCCId() {
		return cccId;
	}

	public void setCCCId(Integer cccId) {
		this.cccId = cccId;
	}

	public Integer getCccId() {
		return cccId;
	}

	public void setCccId(Integer cccId) {
		this.cccId = cccId;
	}

	public Boolean isUseByContracts() {
		return useByContracts;
	}

	public void setUseByContracts(Boolean useByContracts) {
		this.useByContracts = useByContracts;
	}
	
}