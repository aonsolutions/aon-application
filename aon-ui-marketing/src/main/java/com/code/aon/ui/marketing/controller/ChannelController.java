package com.code.aon.ui.marketing.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.registry.Category;
import com.code.aon.registry.enumeration.CategoryType;
import com.code.aon.ui.form.BasicController;

public class ChannelController extends BasicController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private String externalBackAction;
	
	@Override
	public void onReset(ActionEvent event) {
		super.onReset(event);
		Category category = (Category) getTo();
		category.setType(CategoryType.ARTICLE);		
	}
	
	public String getDownloadURL() {
		Category category = (Category) getTo();
		return RSSController.getURL(RSSController.RSS_PREFFIX, category, MimeType.MIME_XML);
	}

	public String getCurrentDownloadURL() throws ManagerBeanException {
		if ( getModel().isRowAvailable() ) {
			Category category = (Category) getModel().getRowData();
			return RSSController.getURL(RSSController.RSS_PREFFIX, category, MimeType.MIME_XML);			
		}
		return null;
	}
	
	public String externalBackAction() {
		String b = getExternalBackAction();
		setExternalBackAction(null);
		return b;
	}
	
	public String getExternalBackAction() {
		return externalBackAction;
	}
	
	public void setExternalBackAction(String externalBackAction) {
		this.externalBackAction = externalBackAction;
	}

	public boolean isBackActionEnabled() {
		return getExternalBackAction() != null;
	}	
}
