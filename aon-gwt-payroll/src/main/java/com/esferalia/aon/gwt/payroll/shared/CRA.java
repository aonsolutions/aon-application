package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("serial")
public class CRA implements Serializable{
	
	private Integer code; //ID UNIQUE
	private Date creationDate;
	private byte status;
	
	private String ccc;
	private String cccType;
	private String cccProvince;
	private String activityName;
	
	public CRA() {
		super();
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public byte getStatus() {
		return status;
	}

	public void setStatus(byte status) {
		this.status = status;
	}

	public String getCcc() {
		return ccc;
	}

	public void setCcc(String ccc) {
		this.ccc = ccc;
	}

	public String getCccType() {
		return cccType;
	}

	public void setCccType(String cccType) {
		this.cccType = cccType;
	}

	public String getCccProvince() {
		return cccProvince;
	}

	public void setCccProvince(String cccProvince) {
		this.cccProvince = cccProvince;
	}

	public String getActivityName() {
		return activityName;
	}

	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}
	
	

}
