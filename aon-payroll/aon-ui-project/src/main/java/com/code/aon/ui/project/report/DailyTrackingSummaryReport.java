package com.code.aon.ui.project.report;


public class DailyTrackingSummaryReport {

	private Integer id;

	private String description;

	private Double duration;

	public DailyTrackingSummaryReport(Integer id, String description, Double duration) {
		this.id = id;
		this.description = description;
		this.duration = duration;
	}


	public Integer getId() {
		if (id==null) return Integer.MIN_VALUE;
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getDescription() {
		if (description==null) return "¿?";
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
}
