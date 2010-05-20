package com.code.aon.ui.cms.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.cms.util.IActivableObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.form.BasicController;

public class BasicActivableController extends BasicController {

	private boolean richTextEnabled;

	public void onActivate(ActionEvent event) throws ManagerBeanException {
		activate(true);
	}

	public void onDeactivate(ActionEvent event) throws ManagerBeanException {
		activate(false);
	}
	
	private void activate(boolean active) throws ManagerBeanException {
		IActivableObject activableObject = (IActivableObject) this.model.getRowData();
		activableObject.setActive(active);
		getManagerBean().update(activableObject);
	}
	
	public boolean isRichTextEnabled() {
		return richTextEnabled;
	}

	public void setRichTextEnabled(boolean richTextEnabled) {
		this.richTextEnabled = richTextEnabled;
	}    
	
}