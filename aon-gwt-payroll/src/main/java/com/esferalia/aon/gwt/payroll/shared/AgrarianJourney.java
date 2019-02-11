package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AgrarianJourney implements Serializable{

	private Integer contractId;
	private Date startDate;
	private Date endDate;
	private Integer totalDays;
	
	private String name;
	private String surname;
	
	public AgrarianJourney(){
		super();
	}
	
	public AgrarianJourney(Integer contractId, Date startDate, Date endDate){
		super();
		this.contractId = contractId;
		this.startDate = startDate;
		this.endDate = endDate;
		this.totalDays = endDate.getDate() - startDate.getDate() + 1;
	}

	public Integer getContractId() {
		return contractId;
	}

	public void setContractId(Integer contractId) {
		this.contractId = contractId;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Integer getTotalDays() {
		return totalDays;
	}

	public void setTotalDays(Integer totalDays) {
		this.totalDays = totalDays;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}
	
}
