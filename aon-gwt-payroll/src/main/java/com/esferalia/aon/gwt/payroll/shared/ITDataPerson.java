package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.Date;

public class ITDataPerson implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1519604556575978934L;
	
	private int contractId;
	private int type;	
	
	private Date leaveStartDate;
	private Date leaveEndDate;	
	
	public ITDataPerson() {
		
	}	

	public int getContractId() {
		return contractId;
	}

	public void setContractId(int contractId) {
		this.contractId = contractId;
	}

	public Date getLeaveStartDate() {
		return leaveStartDate;
	}

	public void setLeaveStartDate(Date start_date) {
		this.leaveStartDate = start_date;
	}

	public Date getLeaveEndDate() {
		return leaveEndDate;
	}

	public void setLeaveEndDate(Date end_date) {
		this.leaveEndDate = end_date;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
}
