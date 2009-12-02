package com.code.aon.ui.cms.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.cms.Link;
import com.code.aon.cms.LinkDetail;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.cms.Constants;


public class LinkController extends BasicI18nController implements Constants {

	private boolean richTextEnabled;
	
	public boolean isRichTextEnabled() {
		return richTextEnabled;
	}

	public void setRichTextEnabled(boolean richTextEnabled) {
		this.richTextEnabled = richTextEnabled;
	}

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
		String label = NO_VALUE_LABEL;
		LinkDetail ld = (LinkDetail)getModelRowdataI18n();
		if (ld != null) label = ld.getLabel();
		return label;
	}

}