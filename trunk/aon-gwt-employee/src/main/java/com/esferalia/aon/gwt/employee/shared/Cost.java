package com.esferalia.aon.gwt.employee.shared;

import java.io.Serializable;
import java.util.Date;

public class Cost implements Serializable {
	
	
	private int month;
	private int year;
	
	private int workplaceId;
	private int enterpriseId;
	
	public int getMonth() {
		return month;
	}
	public void setMonth(int month) {
		this.month = month;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public int getEnterpriseId() {
		return enterpriseId;
	}
	public void setEnterpriseId(int enterpriseId) {
		this.enterpriseId = enterpriseId;
	}
	public int getWorkplaceId() {
		return workplaceId;
	}
	public void setWorkplaceId(int workplaceId) {
		this.workplaceId = workplaceId;
	}
	
	
	
}
