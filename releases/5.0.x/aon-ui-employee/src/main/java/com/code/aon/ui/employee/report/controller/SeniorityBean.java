/**
 * 
 */
package com.code.aon.ui.employee.report.controller;

import java.util.Date;

import com.code.aon.company.resources.Employee;

/**
 * @author Consulting & Development. Iñaki Ayerbe - 13/09/2007
 *
 */
public class SeniorityBean {

	/** Employee. */
	private Employee employee;
	/** Employee starting date */
	private Date startingDate; 
	/** Employee ending date */
	private Date endingDate; 
	/** Employee seniority */
	private Integer seniority; 
	/** Employee seniority type */
	private String seniorityType; 

	/**
	 * @return the employee
	 */
	public Employee getEmployee() {
		return employee;
	}
	/**
	 * @param employee the employee to set
	 */
	public void setEmployee(Employee employee) {
		this.employee = employee;
	}
	/**
	 * @return the endingDate
	 */
	public Date getEndingDate() {
		return endingDate;
	}
	/**
	 * @param endingDate the endingDate to set
	 */
	public void setEndingDate(Date endingDate) {
		this.endingDate = endingDate;
	}
	/**
	 * @return the startingDate
	 */
	public Date getStartingDate() {
		return startingDate;
	}
	/**
	 * @param startingDate the startingDate to set
	 */
	public void setStartingDate(Date startingDate) {
		this.startingDate = startingDate;
	}
	/**
	 * @return the seniority
	 */
	public Integer getSeniority() {
		return seniority;
	}
	/**
	 * @param seniority the seniority to set
	 */
	public void setSeniority(Integer seniority) {
		this.seniority = seniority;
	}

	/**
	 * @return the seniorityType
	 */
	public String getSeniorityType() {
		return seniorityType;
	}
	/**
	 * @param seniorityType the seniorityType to set
	 */
	public void setSeniorityType(String seniorityType) {
		this.seniorityType = seniorityType;
	}

}