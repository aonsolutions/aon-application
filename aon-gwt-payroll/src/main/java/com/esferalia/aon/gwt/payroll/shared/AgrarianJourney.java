package com.esferalia.aon.gwt.payroll.shared;

import static com.esferalia.aon.gwt.payroll.shared.Shared.format;
import static com.esferalia.aon.gwt.payroll.shared.Shared.parse;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AgrarianJourney implements Serializable{

	private Integer contractId;
	private String startDate;
	private String endDate;
	private Integer totalDays;
	
	private String name;
	private String surname;
	
	public AgrarianJourney(){
		super();
	}
	
	public AgrarianJourney(Integer contractId, Date startDate, Date endDate){
		super();
		this.contractId = contractId;
		this.startDate = format(startDate);
		this.endDate = format(endDate);
		this.totalDays = endDate.getDate() - startDate.getDate() + 1;
	}

	public Integer getContractId() {
		return contractId;
	}

	public void setContractId(Integer contractId) {
		this.contractId = contractId;
	}

	public Date getStartDate() {
		return parse(startDate);
	}

	public void setStartDate(Date startDate) {
		this.startDate = format(startDate);
	}

	public Date getEndDate() {
		return parse(endDate);
	}

	public void setEndDate(Date endDate) {
		this.endDate = format(endDate);
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
