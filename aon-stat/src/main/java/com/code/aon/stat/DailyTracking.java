package com.code.aon.stat;

import java.io.Serializable;
import java.util.Date;

import com.code.aon.AonVersion;
import com.code.aon.common.util.CommonUtil;

public class DailyTracking implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private Integer id;
	private Date trackingDate;
	private String comments;
	private String taskHolderName;
	private String jobDescription;
	private String registryName;
	private double trackingDuration;
	private double cost;

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Date getTrackingDate() {
		return trackingDate;
	}
	public void setTrackingDate(Date trackingDate) {
		this.trackingDate = trackingDate;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public String getTaskHolderName() {
		return taskHolderName;
	}
	public void setTaskHolderName(String taskHolderName) {
		this.taskHolderName = taskHolderName;
	}
	public String getJobDescription() {
		return jobDescription;
	}
	public void setJobDescription(String jobDescription) {
		this.jobDescription = jobDescription;
	}
	public String getRegistryName() {
		return registryName;
	}
	public void setRegistryName(String registryName) {
		this.registryName = registryName;
	}
	public double getTrackingDuration() {
		return trackingDuration;
	}
	public void setTrackingDuration(double trackingDuration) {
		this.trackingDuration = trackingDuration;
	}
	public double getCost() {
		return cost;
	}
	public void setCost(double cost) {
		this.cost = cost;
	}
	public double getAmount() {
		return CommonUtil.round( getTrackingDuration() * getCost() );
	}
	
}
