package com.code.aon.ui.cms.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.cms.SportPositionDetail;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.cms.Constants;


public class SportPositionController extends BasicI18nController implements Constants {

	@SuppressWarnings("unused")
	public void onSelect(ActionEvent event) {
		super.onSelect(event);
		loadCurrentLanguage();
	}

	public String getI18nDescription() throws ManagerBeanException {
		String description = NO_VALUE_LABEL;
		SportPositionDetail detail = (SportPositionDetail)getModelRowdataI18n();
		if (detail != null) description = detail.getDescription();
		return description;
	}

}