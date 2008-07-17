package com.code.aon.ui.cms.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.cms.ActivityDetail;
import com.code.aon.common.ManagerBeanException;


public class ActivityController extends BasicI18nController {

	private int page;
	
	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	@SuppressWarnings("unused")
	public void onSelect(ActionEvent event) {
		super.onSelect(event);
		loadCurrentLanguage();
	}

	public String getI18nDescription() throws ManagerBeanException {
		String description = "- NO VALUE -";
		ActivityDetail detail = (ActivityDetail)getModelRowdataI18n();
		if (detail != null) description = detail.getDescription();
		return description;
	}

}