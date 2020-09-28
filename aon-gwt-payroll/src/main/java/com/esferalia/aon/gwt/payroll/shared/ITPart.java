package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.Date;

public class ITPart implements Serializable {
	
	private static final long serialVersionUID = 1L;
		
	private Integer domain;
	private Byte type;
	private Integer it;
	private String collegeNumber;
	private Byte confirmOrderNumber;
	private String cias;
	private Date date;
	private Byte status;
	
	public ITPart() {
		super();
	}

	public Integer getDomain() {
		return domain;
	}

	public void setDomain(Integer domain) {
		this.domain = domain;
	}

	public Byte getType() {
		return null == type ? 0 : type;
	}

	public void setType(Byte type) {
		this.type = type;
	}

	public Integer getIt() {
		return it;
	}

	public void setIt(Integer it) {
		this.it = it;
	}

	public String getCollegeNumber() {
		return collegeNumber;
	}

	public void setCollegeNumber(String collegeNumber) {
		this.collegeNumber = collegeNumber;
	}

	public Byte getConfirmOrderNumber() {
		return confirmOrderNumber;
	}

	public void setConfirmOrderNumber(Byte confirmOrderNumber) {
		this.confirmOrderNumber = confirmOrderNumber;
	}

	public String getCias() {
		return cias;
	}

	public void setCias(String cias) {
		this.cias = cias;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Byte getStatus() {
		return null == status ? (byte) 0 : status;
	}

	public void setStatus(Byte status) {
		this.status = status;
	}
		
}
