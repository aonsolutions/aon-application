package com.code.aon.ui.cms.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.cms.Link;
import com.code.aon.cms.LinkDetail;
import com.code.aon.common.ManagerBeanException;


public class LinkController extends BasicI18nController {

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
		Link l = (Link)this.model.getRowData();
		l.setActive(active);
		getManagerBean().update(l);
	}
	
	public String getI18nLabel() throws ManagerBeanException {
		String label = "- NO VALUE -";
		LinkDetail ld = (LinkDetail)getModelRowdataI18n();
		if (ld != null) label = ld.getLabel();
		return label;
	}

}