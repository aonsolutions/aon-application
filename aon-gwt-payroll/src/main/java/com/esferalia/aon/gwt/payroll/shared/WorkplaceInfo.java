package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.Map;

@SuppressWarnings("serial")
public class WorkplaceInfo implements Serializable {

	//General Data
	private String description;
	private Map<Integer, String> addresses;
	private Integer addressId;
	private byte economicConcert;
	private byte active;
	
	//Payroll Data
	private Map<Integer, String> calendar;
	private Integer calendarId;
	private Integer agreementId;
	private Map<Integer, String> activities;
	private Integer activityId;
	
	//WorkplaceID
	private Integer workplaceId;
	private Integer payrollWorkplaceId;
	
	public WorkplaceInfo(){
		
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Map<Integer, String> getAddresses() {
		return addresses;
	}

	public void setAddresses(Map<Integer, String> addresses) {
		this.addresses = addresses;
	}

	public Integer getAddressId() {
		return addressId;
	}

	public void setAddressId(Integer addressId) {
		this.addressId = addressId;
	}

	public byte getEconomicConcert() {
		return economicConcert;
	}

	public void setEconomicConcert(byte economicConcert) {
		this.economicConcert = economicConcert;
	}

	public byte isActive() {
		return active;
	}

	public void setActive(byte active) {
		this.active = active;
	}

	public Map<Integer, String> getCalendars() {
		return calendar;
	}

	public void setCalendar(Map<Integer, String> calendar) {
		this.calendar = calendar;
	}

	public Integer getAgreementId() {
		return agreementId;
	}

	public void setAgreementId(Integer agreementId) {
		this.agreementId = agreementId;
	}

	public Map<Integer, String> getActivities() {
		return activities;
	}

	public void setActivities(Map<Integer, String> activities) {
		this.activities = activities;
	}

	public Integer getCalendarId() {
		return calendarId;
	}

	public void setCalendarId(Integer calendarId) {
		this.calendarId = calendarId;
	}

	public Integer getActivityId() {
		return activityId;
	}

	public void setActivityId(Integer activityId) {
		this.activityId = activityId;
	}
	
	public Integer getWorkplaceId() {
		return this.workplaceId;
	}

	public void setWorkplaceId(Integer workplaceId) {
		this.workplaceId = workplaceId;
	}

	public Integer getPayrollWorkplaceId() {
		return this.payrollWorkplaceId;
	}
	
	public void setPayrollWorkplaceId(Integer payrollWorkplaceId) {
		this.payrollWorkplaceId = payrollWorkplaceId;
	}
	
}
