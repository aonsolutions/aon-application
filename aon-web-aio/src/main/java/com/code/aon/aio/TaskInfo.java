package com.code.aon.aio;


public class TaskInfo {

	private String description;
	
	private String total;

	public TaskInfo(String description, String total) {
		this.description = description;
		this.total = total;
	}

	public String getDescription() {
		return description;
	}

	public String getTotal() {
		return total;
	}
}
