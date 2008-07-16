package com.code.aon.ui.cms.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.cms.DirectAccessGroup;
import com.code.aon.cms.DirectAccessGroupDetail;
import com.code.aon.common.ManagerBeanException;


public class DirectAccessGroupController extends BasicI18nController {

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

	public void onActivate(ActionEvent event) throws ManagerBeanException {
		activate(true);
	}

	public void onDeactivate(ActionEvent event) throws ManagerBeanException {
		activate(false);
	}
	
	private void activate(boolean active) throws ManagerBeanException {
		DirectAccessGroup directAccessGroup = (DirectAccessGroup)this.model.getRowData();
		directAccessGroup.setActive(active);
		getManagerBean().update(directAccessGroup);
	}
	
	public String getI18nLabel() throws ManagerBeanException {
		String label = "- NO VALUE -";
		DirectAccessGroupDetail directAccessGroupDetail = (DirectAccessGroupDetail)getModelRowdataI18n();
		if (directAccessGroupDetail != null) label = directAccessGroupDetail.getLabel();
		return label;
	}

}