package com.code.aon.ui.cms.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.cms.Download;
import com.code.aon.cms.DownloadDetail;
import com.code.aon.cms.Image;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.util.AonUtil;

public class DownloadController extends BasicI18nController{

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
		Download d = (Download)this.model.getRowData();
		d.setActive(active);
		getManagerBean().update(d);
	}
	
	public String getI18nTitle() throws ManagerBeanException {
		String title = "- NO VALUE -";
		DownloadDetail downloadDetail = (DownloadDetail)getModelRowdataI18n();
		if (downloadDetail != null) title = downloadDetail.getTitle();
		return title;
	}

	public void onDelImage(ActionEvent event) {
		DownloadDetail current = (DownloadDetail)getToI18n();
		current.setFile(null);
	}

	public void onSelectImage(ActionEvent event) throws ManagerBeanException {
		GalleryController controller = (GalleryController)AonUtil.getRegisteredBean("document");
		String image = ((Image)controller.getModel().getRowData()).getRelativePath();
		DownloadDetail current = (DownloadDetail)getToI18n();
		current.setFile(image);
	}

}