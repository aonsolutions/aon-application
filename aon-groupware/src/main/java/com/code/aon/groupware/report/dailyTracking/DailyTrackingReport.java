package com.code.aon.groupware.report.dailyTracking;

import java.util.Date;

import com.code.aon.common.util.CommonUtil;

public class DailyTrackingReport {

	private static final String UNKNOWN = "?";

	private Integer id;
	private Integer userId;
	private String userName;
	private Date date;
	private Double duration;
	private Double cost;
	private Integer jobTypeId;
	private String jobTypeDescription;
	private Integer registryId;
	private String registryName;
	private Integer projectId;
	private String projectName;
	private Integer activityTypeId;
	private String activityTypeDescription;
	private Integer projectTypeId;
	private String projectTypeDescription;
	private String comments;
	
	public DailyTrackingReport(Integer id, Integer userId, String userName, Date date,
			Double duration,Double cost, Integer jobTypeId, String jobTypeDescription, Integer registryId,
			String registryName,Integer projectId, String projectName, Integer activityTypeId,
			String activityTypeDescription, Integer projectTypeId, String projectTypeDescription,String comments) {
		this.id = id;
		this.userId = userId;
		this.userName = userName;
		this.date = date;
		this.duration = duration;
		this.cost = cost;
		this.jobTypeId = jobTypeId;
		this.jobTypeDescription = jobTypeDescription;
		this.registryId = registryId;
		this.registryName = registryName;
		this.projectId = projectId;
		this.projectName = projectName;
		this.activityTypeId = activityTypeId;
		this.activityTypeDescription = activityTypeDescription;
		this.projectTypeId = projectTypeId;
		this.projectTypeDescription = projectTypeDescription;
		this.comments = comments;
	}

	public DailyTrackingReport(Integer userId, String userName, 
			Double duration, Integer jobTypeId, String jobTypeDescription, Integer registryId,
			String registryName) {
		this.userId = userId;
		this.userName = userName;
		this.duration = duration;
		this.jobTypeId = jobTypeId;
		this.jobTypeDescription = jobTypeDescription;
		this.registryId = registryId;
		this.registryName = registryName;
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

	public Double getCost() {
		return cost;
	}
	public void setCost(Double cost) {
		this.cost = cost;
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

	public Integer getRegistryId() {
		if (registryId==null) return Integer.MIN_VALUE;
		return registryId;
	}
	public void setRegistryId(Integer registryId) {
		this.registryId = registryId;
	}

	public String getRegistryName() {
		if (registryName==null) return UNKNOWN;
		return registryName;
	}
	public void setRegistryName(String registryName) {
		this.registryName = registryName;
	}

	public Integer getProjectId() {
		if (projectId==null) return Integer.MIN_VALUE;
		return projectId;
	}
	public void setProjectId(Integer projectId) {
		this.projectId = projectId;
	}

	public String getProjectName() {
		if (projectName==null) return UNKNOWN;
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName= projectName;
	}

	public Integer getActivityTypeId() {
		if (activityTypeId==null) return Integer.MIN_VALUE;
		return activityTypeId;
	}
	public void setActivityTypeId(Integer activityTypeId) {
		this.activityTypeId = activityTypeId;
	}

	public String getActivityTypeDescription() {
		if (activityTypeDescription==null) return UNKNOWN;
		return activityTypeDescription;
	}
	public void setActivityTypeDescription(String activityDescription) {
		this.activityTypeDescription = activityDescription;
	}

	public Integer getProjectTypeId() {
		if (projectTypeId==null) return Integer.MIN_VALUE;
		return projectTypeId;
	}
	public void setProjectTypeId(Integer projectTypeId) {
		this.projectTypeId = projectTypeId;
	}

	public String getProjectTypeDescription() {
		if (projectTypeDescription==null) return UNKNOWN;
		return projectTypeDescription;
	}
	public void setProjectTypeDescription(String projectTypeDescription) {
		this.projectTypeDescription = projectTypeDescription;
	}

	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	
	public double getAmount() {
		return CommonUtil.round(getDuration() * getCost());
	}
}
