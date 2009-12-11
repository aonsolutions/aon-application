package com.code.aon.ui.cms.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.cms.SportPositionDetail;
import com.code.aon.common.ManagerBeanException;


public class SportPositionController extends BasicI18nController {

	@SuppressWarnings("unused")
	public void onSelect(ActionEvent event) {
		super.onSelect(event);
		loadCurrentLanguage();
	}

	public String getI18nDescription() throws ManagerBeanException {
		String description = "- NO VALUE -";
		SportPositionDetail detail = (SportPositionDetail)getModelRowdataI18n();
		if (detail != null) description = detail.getDescription();
		return description;
	}

}