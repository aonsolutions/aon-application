package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;

public class Cost implements Serializable {

	private int month;
	private int year;

	private int workplaceId;
	private int enterpriseId;

	private int extrasCount;
	private int delaysCount;
	private int settlesCount;
	private int salariesCount;
	private int proceduralCount;

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

	public int getExtrasCount() {
		return extrasCount;
	}

	public void setExtrasCount(int extrasCount) {
		this.extrasCount = extrasCount;
	}

	public int getDelaysCount() {
		return delaysCount;
	}

	public void setDelaysCount(int delaysCount) {
		this.delaysCount = delaysCount;
	}

	public int getSettlesCount() {
		return settlesCount;
	}

	public void setSettlesCount(int settlesCount) {
		this.settlesCount = settlesCount;
	}

	public int getSalariesCount() {
		return salariesCount;
	}

	public void setSalariesCount(int salariesCount) {
		this.salariesCount = salariesCount;
	}
	
	public int getProceduralCount() {
		return proceduralCount;
	}
	
	public void setProceduralCount(int proceduralCount) {
		this.proceduralCount = proceduralCount;
	} 

}
