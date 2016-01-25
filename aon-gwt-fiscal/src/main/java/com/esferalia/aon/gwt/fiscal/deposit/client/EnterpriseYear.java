package com.esferalia.aon.gwt.fiscal.deposit.client;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.Enterprise;

public class EnterpriseYear implements Serializable {
	
	private static final long serialVersionUID = 241904715048227424L;
	
	private Integer year;
	private Enterprise enterprise;
	
	public Integer getYear() {
		return year;
	}
	public void setYear(Integer year) {
		this.year = year;
	}
	public Enterprise getEnterprise() {
		return enterprise;
	}
	public void setEnterprise(Enterprise enterprise) {
		this.enterprise = enterprise;
	}
	

}
