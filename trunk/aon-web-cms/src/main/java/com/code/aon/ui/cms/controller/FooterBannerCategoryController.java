package com.code.aon.ui.cms.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.cms.Footer;
import com.code.aon.ui.form.GridController;

public class FooterBannerCategoryController extends GridController {

	private Footer currentFooter;

	
	public Footer getCurrentFooter() {
		return currentFooter;
	}


	public void setCurrentFooter(Footer currentFooter) {
		this.currentFooter = currentFooter;
	}


	public void onRemoveCurrent(ActionEvent event) {
		this.setRowChecked(true);
		this.onRemoveSelected(event);
	}
}
