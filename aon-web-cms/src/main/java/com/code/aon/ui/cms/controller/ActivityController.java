package com.code.aon.ui.cms.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.cms.ActivityDetail;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.cms.Constants;
import com.code.aon.ui.form.FormUtil;


public class ActivityController extends BasicI18nController implements ICMSConstants, Constants {

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
		String description = NO_VALUE_LABEL;
		ActivityDetail detail = (ActivityDetail)getModelRowdataI18n();
		if (detail != null) description = detail.getDescription();
		return description;
	}

	public String getBack(){
		if (FormUtil.getController(COMPANY).getTo()==null)
			return COMPANY_LIST;
		return COMPANY_FORM;
	}

}