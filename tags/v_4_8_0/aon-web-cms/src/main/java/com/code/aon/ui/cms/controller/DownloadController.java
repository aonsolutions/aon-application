package com.code.aon.ui.cms.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.cms.Download;
import com.code.aon.cms.DownloadDetail;
import com.code.aon.cms.Image;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.cms.Constants;
import com.code.aon.ui.util.AonUtil;

public class DownloadController extends BasicI18nController implements ICMSConstants, Constants {

	private boolean showWindow;
	
	private boolean richTextEnabled;

	public boolean isRichTextEnabled() {
		return richTextEnabled;
	}

	public void setRichTextEnabled(boolean richTextEnabled) {
		this.richTextEnabled = richTextEnabled;
	}
	
	public boolean isShowWindow() {
		return showWindow;
	}

	public void setShowWindow(boolean showWindow) {
		this.showWindow = showWindow;
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
		Download d = (Download)this.model.getRowData();
		d.setActive(active);
		getManagerBean().update(d);
	}
	
	public String getI18nTitle() throws ManagerBeanException {
		String title = NO_VALUE_LABEL;
		DownloadDetail downloadDetail = (DownloadDetail)getModelRowdataI18n();
		if (downloadDetail != null) title = downloadDetail.getTitle();
		return title;
	}

	public void onDelDocument(ActionEvent event) {
		DownloadDetail current = (DownloadDetail)getToI18n();
		current.setFile(null);
	}

	public void onSelectImage(ActionEvent event) throws ManagerBeanException {
		GalleryController controller = (GalleryController)AonUtil.getRegisteredBean(DOCUMENT);
		String image = ((Image)controller.getModel().getRowData()).getRelativePath();
		DownloadDetail current = (DownloadDetail)getToI18n();
		current.setFile(image);
	}
	
}