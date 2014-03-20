package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.Date;

public class ITPruebaDatas implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3071723297747337076L;
	
	private int registry;
	private String name;
	private String firstSurname;
	private String secondSurname;
	private String fullName;
	private int cotractId;
	private Date contract_startDate;
	private Date cotract_endDate;
	private Date leave_startDate;
	private Date leave_endDate;
	
	public ITPruebaDatas() {		

	}

	public int getRegistry() {
		return registry;
	}

	public void setRegistry(int registry) {
		this.registry = registry;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFirstSurname() {
		return firstSurname;
	}

	public void setFirstSurname(String firstSurname) {
		this.firstSurname = firstSurname;
	}

	public String getSecondSurname() {
		return secondSurname;
	}

	public void setSecondSurname(String secondSurname) {
		this.secondSurname = secondSurname;
	}

	public String getFullName() {
		return getName() + " " + getFirstSurname();
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public int getCotractId() {
		return cotractId;
	}

	public void setCotractId(int cotractId) {
		this.cotractId = cotractId;
	}

	public Date getContract_startDate() {
		return contract_startDate;
	}

	public void setContract_startDate(Date contract_startDate) {
		this.contract_startDate = contract_startDate;
	}

	public Date getCotract_endDate() {
		return cotract_endDate;
	}

	public void setCotract_endDate(Date cotract_endDate) {
		this.cotract_endDate = cotract_endDate;
	}

	public Date getLeave_startDate() {
		return leave_startDate;
	}

	public void setLeave_startDate(Date leave_startDate) {
		this.leave_startDate = leave_startDate;
	}

	public Date getLeave_endDate() {
		return leave_endDate;
	}

	public void setLeave_endDate(Date leave_endDate) {
		this.leave_endDate = leave_endDate;
	}
	
	
}
