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
	private Map<Integer, String> scopes;
	private Integer scopeId;
	private byte active;
	
	//Payroll Data
	private Map<Integer, String> calendar;
	private Integer calendarId;
	private Integer agreementId;
	private String agreementDescription;
	private Map<Integer, String> activities;
	private Integer activityId;
	
	//WorkplaceID
	private Integer workplaceId;
	private Integer payrollWorkplaceId;
	
	public WorkplaceInfo(){
		
	}
	
	public WorkplaceInfo(WorkplaceInfo workplaceInfo){
		this.description = workplaceInfo.getDescription();
		this.addressId = workplaceInfo.getAddressId();
		this.economicConcert = workplaceInfo.getEconomicConcert();
		this.active = workplaceInfo.isActive();
		this.calendarId = workplaceInfo.getCalendarId();
		this.agreementId = workplaceInfo.getAgreementId();
		this.agreementDescription = workplaceInfo.getAgreementDescription();
		this.activityId = workplaceInfo.getActivityId();
		this.workplaceId = workplaceInfo.getWorkplaceId();
		this.payrollWorkplaceId = workplaceInfo.getPayrollWorkplaceId();
	}
	
	public boolean hasChanged (WorkplaceInfo workplaceInfo_old){
		
//		Window.alert("Description -> " + this.description + " == " + workplaceInfo_old.getDescription());
		if (this.description != workplaceInfo_old.getDescription()) return true;
//		Window.alert("Address -> " + this.addressId + " == " + workplaceInfo_old.getAddressId());
		if (!this.addressId.equals(workplaceInfo_old.getAddressId())) return true;
//		Window.alert("EconomicConcert -> " + this.economicConcert + " == " + workplaceInfo_old.getEconomicConcert());
		if (this.economicConcert != workplaceInfo_old.getEconomicConcert()) return true;
//		Window.alert("Active -> " + this.active + " == " + workplaceInfo_old.isActive());
		if (this.active != workplaceInfo_old.isActive()) return true;
		
//		Window.alert("Calendar -> " + this.calendarId + " == " + workplaceInfo_old.getCalendarId());
		if(null == this.calendarId || null == workplaceInfo_old.getCalendarId()) {
			if (this.calendarId != workplaceInfo_old.getCalendarId()) return true;
		}else
			if (!this.calendarId.equals(workplaceInfo_old.getCalendarId())) return true;
		
//		Window.alert("Agreement -> " + this.agreementId + " == " + workplaceInfo_old.getAgreementId());
		if(null == this.agreementId || null == workplaceInfo_old.getAgreementId()) {
			if (this.agreementId != workplaceInfo_old.getAgreementId()) return true;
		}else
			if (!this.agreementId.equals(workplaceInfo_old.getAgreementId())) return true;
		
//		Window.alert("AgreementDescription -> " + this.agreementDescription + " == " + workplaceInfo_old.getAgreementDescription());
		if (this.agreementDescription != workplaceInfo_old.getAgreementDescription()) return true;
		
//		Window.alert("Activity -> " + this.activityId + " == " + workplaceInfo_old.getActivityId());
		if(null == this.activityId || null == workplaceInfo_old.getActivityId()) {
			if (this.activityId != workplaceInfo_old.getActivityId()) return true;
		}else
			if (!this.activityId.equals(workplaceInfo_old.getActivityId())) return true;
				
//		Window.alert("Workplace -> " + this.workplaceId + " == " + workplaceInfo_old.getWorkplaceId());
		if (!this.workplaceId.equals(workplaceInfo_old.getWorkplaceId())) return true;
//		Window.alert("PayrollWorkplace -> " + this.payrollWorkplaceId + " == " + workplaceInfo_old.getPayrollWorkplaceId());
		if (!this.payrollWorkplaceId.equals(workplaceInfo_old.getPayrollWorkplaceId())) return true;
		
		return false; // Si no ha cambiado nada
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

	public Map<Integer, String> getScopes() {
		return scopes;
	}

	public void setScopes(Map<Integer, String> scopes) {
		this.scopes = scopes;
	}

	public Integer getScopeId() {
		return scopeId;
	}

	public void setScopeId(Integer scopeId) {
		this.scopeId = scopeId;
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
	
	public String getAgreementDescription() {
		return this.agreementDescription;
	}
	
	public void setAgreementDescription(String agreementDescription) {
		this.agreementDescription = agreementDescription;
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
