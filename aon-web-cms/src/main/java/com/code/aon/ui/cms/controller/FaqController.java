package com.code.aon.ui.cms.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.cms.Faq;
import com.code.aon.cms.FaqDetail;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.cms.Constants;

public class FaqController extends BasicI18nController implements Constants {

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
		Faq f = (Faq)this.model.getRowData();
		f.setActive(active);
		getManagerBean().update(f);
	}
	
	public String getI18nAnswer() throws ManagerBeanException {
		String label = NO_VALUE_LABEL;
		FaqDetail fd = (FaqDetail)getModelRowdataI18n();
		if (fd != null) label = fd.getAnswer();
		return label;
	}

	public String getI18nQuestion() throws ManagerBeanException {
		String label = NO_VALUE_LABEL;
		FaqDetail fd = (FaqDetail)getModelRowdataI18n();
		if (fd != null) label = fd.getQuestion();
		return label;
	}

}