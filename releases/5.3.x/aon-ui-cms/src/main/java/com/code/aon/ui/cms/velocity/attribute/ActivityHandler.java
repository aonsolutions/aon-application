package com.code.aon.ui.cms.velocity.attribute;

import java.util.List;

import com.code.aon.cms.ActivityDetail;

public class ActivityHandler {

	private String description;

	private List<CompanyHandler> list;
	
	public ActivityHandler (ActivityDetail detail, List<CompanyHandler> list) {
		this.description = detail.getDescription();
		this.list = list;
	}

	public String getDescription() {
		return description;
	}

	public List<CompanyHandler> getList() {
		return list;
	}

}
