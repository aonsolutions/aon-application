package com.code.aon.groupware.report.dailyTracking;

import com.code.aon.common.util.CommonUtil;


public class DailyTrackingSummaryReport {
	
	private static final String QUESTION_MARK = "?";
	
	private Integer id;
	private String description;
	private Double duration;
	private Double cost;

	public DailyTrackingSummaryReport(Integer id, String description, Double duration, Double cost) {
		this.id = id;
		this.description = description;
		this.duration = duration;
		this.cost = cost;
	}

	public Integer getId() {
		if (id==null) return Integer.MIN_VALUE;
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}

	public String getDescription() {
		if (description==null) return QUESTION_MARK;
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
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
	
	public Double getAmount() {
		return CommonUtil.round(getCost() * getDuration());
	}
	
}
