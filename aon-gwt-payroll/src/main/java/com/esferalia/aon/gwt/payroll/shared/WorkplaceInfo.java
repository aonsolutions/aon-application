package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;

@SuppressWarnings("serial")
public class WorkplaceInfo implements Serializable {

	//General Data
	private Integer domain;
	private String description;
	private Integer addressId;
	private byte economicConcert;
	
	//Payroll Data
	private String calendarDescription;
	private Integer agreementId;
	private Integer activityId;
	
	//WorkplaceID
	private Integer workplaceId;
	private Integer payrollWorkplaceId;
	
	public WorkplaceInfo(){}
	
	public WorkplaceInfo(WorkplaceInfo workplaceInfo){
		this.domain = workplaceInfo.getDomain();
		this.description = workplaceInfo.getDescription();
		this.addressId = workplaceInfo.getAddressId();
		this.economicConcert = workplaceInfo.getEconomicConcert();
		this.calendarDescription = workplaceInfo.getCalendarDescription();
		this.agreementId = workplaceInfo.getAgreementId();
		this.activityId = workplaceInfo.getActivityId();
		this.workplaceId = workplaceInfo.getWorkplaceId();
		this.payrollWorkplaceId = workplaceInfo.getPayrollWorkplaceId();
	}
	
	public boolean hasChanged (WorkplaceInfo workplaceInfo_old){
		if (this.description != workplaceInfo_old.getDescription()) return true;
		if (!this.addressId.equals(workplaceInfo_old.getAddressId())) return true;
		if (this.economicConcert != workplaceInfo_old.getEconomicConcert()) return true;
		
		if(null == this.agreementId || null == workplaceInfo_old.getAgreementId()) {
			if (this.agreementId != workplaceInfo_old.getAgreementId()) return true;
		}else
			if (!this.agreementId.equals(workplaceInfo_old.getAgreementId())) return true;
		
		if(null == this.activityId || null == workplaceInfo_old.getActivityId()) {
			if (this.activityId != workplaceInfo_old.getActivityId()) return true;
		}else
			if (!this.activityId.equals(workplaceInfo_old.getActivityId())) return true;
		
		if (!this.workplaceId.equals(workplaceInfo_old.getWorkplaceId())) return true;
		if (!this.payrollWorkplaceId.equals(workplaceInfo_old.getPayrollWorkplaceId())) return true;
		
		return false; // Si no ha cambiado nada
	}
	
	public Integer getDomain() {
		return this.domain;
	}
	
	public void setDomain (Integer domain) {
		this.domain = domain;
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
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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
	
	public String getCalendarDescription() {
		return calendarDescription;
	}

	public void setCalendarDescription(String calendarDescription) {
		this.calendarDescription = calendarDescription;
	}

	public Integer getAgreementId() {
		return agreementId;
	}

	public void setAgreementId(Integer agreementId) {
		this.agreementId = agreementId;
	}

	public Integer getActivityId() {
		return activityId;
	}

	public void setActivityId(Integer activityId) {
		this.activityId = activityId;
	}
	
}
