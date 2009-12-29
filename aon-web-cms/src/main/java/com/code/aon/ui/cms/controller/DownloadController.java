package com.code.aon.ui.cms.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.cms.Download;
import com.code.aon.cms.DownloadDetail;
import com.code.aon.cms.Image;
import com.code.aon.cms.enumeration.ContentLevel;
import com.code.aon.cms.enumeration.PageType;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.cms.Constants;
import com.code.aon.ui.cms.util.ReferenceChecker;
import com.code.aon.ui.util.AonUtil;

public class DownloadController extends BasicI18nController implements ICMSConstants, Constants {

	private boolean showWindow;
	
	public boolean isShowWindow() {
		return showWindow;
	}

	public void setShowWindow(boolean showWindow) {
		this.showWindow = showWindow;
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

	public boolean isUsed() throws ManagerBeanException {
		Integer id = ((Download) getTo()).getId();
		return ReferenceChecker.isInDirectAccess(id, ContentLevel.ELEMENT, PageType.DOWNLOAD);
	}	
	
}