package com.code.aon.ui.project.report;

import java.util.Date;

public class DailyTrackingReport {

	private Integer id;

	private Integer userId;

	private String userName;

	private Date date;

	private Double duration;

	private Integer jobTypeId;

	private String jobTypeDescription;

	private Integer customerId;

	private String customerName;

	private Integer dossierId;

	private String dossierNumber;

	private Integer activityId;

	private String activityDescription;

	private Integer dossierTypeId;

	private String dossierTypeDescription;
	
	private String comments;
	
	public String getComments() {
		return comments;
	}


	public void setComments(String comments) {
		this.comments = comments;
	}


	private static final String UNKNOWN = "?";

	public DailyTrackingReport(Integer id, Integer userId, String userName, Date date,
			Double duration, Integer jobTypeId, String jobTypeDescription, Integer customerId,
			String customerName,Integer dossierId, String dossierNumber, Integer activityId,
			String activityDescription, Integer dossierTypeId, String dossierTypeDescription,String comments) {
		this.id = id;
		this.userId = userId;
		this.userName = userName;
		this.date = date;
		this.duration = duration;
		this.jobTypeId = jobTypeId;
		this.jobTypeDescription = jobTypeDescription;
		this.customerId = customerId;
		this.customerName = customerName;
		this.dossierId = dossierId;
		this.dossierNumber = dossierNumber;
		this.activityId = activityId;
		this.activityDescription = activityDescription;
		this.dossierTypeId = dossierTypeId;
		this.dossierTypeDescription = dossierTypeDescription;
		this.comments = comments;
	}


	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Double getDuration() {
		return duration;
	}

	public void setDuration(Double duration) {
		this.duration = duration;
	}

	public Integer getJobTypeId() {
		return jobTypeId;
	}

	public void setJobTypeId(Integer jobTypeId) {
		this.jobTypeId = jobTypeId;
	}

	public String getJobTypeDescription() {
		return jobTypeDescription;
	}

	public void setJobTypeDescription(String jobTypeDescription) {
		this.jobTypeDescription = jobTypeDescription;
	}

	public Integer getCustomerId() {
		if (customerId==null) return Integer.MIN_VALUE;
		return customerId;
	}

	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}

	public String getCustomerName() {
		if (customerName==null) return UNKNOWN;
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public Integer getDossierId() {
		if (dossierId==null) return Integer.MIN_VALUE;
		return dossierId;
	}

	public void setDossierId(Integer dossierId) {
		this.dossierId = dossierId;
	}

	public String getDossierNumber() {
		if (dossierNumber==null) return UNKNOWN;
		return dossierNumber;
	}

	public void setDossierNumber(String dossierNumber) {
		this.dossierNumber = dossierNumber;
	}

	public Integer getActivityId() {
		if (activityId==null) return Integer.MIN_VALUE;
		return activityId;
	}

	public void setActivityId(Integer activityId) {
		this.activityId = activityId;
	}

	public String getActivityDescription() {
		if (activityDescription==null) return UNKNOWN;
		return activityDescription;
	}

	public void setActivityDescription(String activityDescription) {
		this.activityDescription = activityDescription;
	}


	public Integer getDossierTypeId() {
		if (dossierTypeId==null) return Integer.MIN_VALUE;
		return dossierTypeId;
	}


	public void setDossierTypeId(Integer dossierTypeId) {
		this.dossierTypeId = dossierTypeId;
	}


	public String getDossierTypeDescription() {
		if (dossierTypeDescription==null) return UNKNOWN;
		return dossierTypeDescription;
	}


	public void setDossierTypeDescription(String dossierTypeDescription) {
		this.dossierTypeDescription = dossierTypeDescription;
	}

}
