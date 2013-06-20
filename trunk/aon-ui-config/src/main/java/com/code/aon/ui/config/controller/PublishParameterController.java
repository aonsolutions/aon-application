package com.code.aon.ui.config.controller;

import static com.code.aon.common.enumeration.AppParam.WEBINFO_FTP_PASSWORD;
import static com.code.aon.common.enumeration.AppParam.WEBINFO_FTP_SERVER;
import static com.code.aon.common.enumeration.AppParam.WEBINFO_FTP_USER;
import static com.code.aon.common.enumeration.AppParam.WEBINFO_PREVIEW_PATH;
import static com.code.aon.common.enumeration.AppParam.WEBINFO_PREVIEW_URL;
import static com.code.aon.common.enumeration.AppParam.WEBINFO_PUBLISH_PATH;
import static com.code.aon.common.enumeration.AppParam.WEBINFO_PUBLISH_URL;

import javax.faces.event.ActionEvent;

import com.code.aon.config.util.AppParamUtil;
import com.code.aon.ui.config.PublishProperties;

public class PublishParameterController {

	private PublishProperties to;
	
	private boolean showOnlyFTP;
	
	public void onInit( ActionEvent event ) {
		this.to = getPublishProperties();
	}
	
	public boolean isShowOnlyFTP() {
		return showOnlyFTP;
	}

	public void setShowOnlyFTP(boolean showOnlyFTP) {
		this.showOnlyFTP = showOnlyFTP;
	}

	public PublishProperties getTo() {
		return to;
	}

	public void setTo(PublishProperties to) {
		this.to = to;
	}

	public void accept(ActionEvent event) {
		AppParamUtil.insertParameter(WEBINFO_FTP_SERVER, to.getFtpServer());
		AppParamUtil.insertParameter(WEBINFO_FTP_USER, to.getFtpUser());
		AppParamUtil.insertParameter(WEBINFO_FTP_PASSWORD, to.getFtpPassword());
		AppParamUtil.insertParameter(WEBINFO_PREVIEW_PATH, to.getPreviewPath());
		AppParamUtil.insertParameter(WEBINFO_PREVIEW_URL, to.getPreviewURL());
		AppParamUtil.insertParameter(WEBINFO_PUBLISH_PATH, to.getPublishPath());
		AppParamUtil.insertParameter(WEBINFO_PUBLISH_URL, to.getPublishURL());
	}
	
	public PublishProperties getPublishProperties() {
		PublishProperties fp = new PublishProperties();
		fp.setFtpServer(AppParamUtil.getValue(WEBINFO_FTP_SERVER));
		fp.setFtpUser(AppParamUtil.getValue(WEBINFO_FTP_USER));
		fp.setFtpPassword(AppParamUtil.getValue(WEBINFO_FTP_PASSWORD));
		fp.setPreviewPath(AppParamUtil.getValue(WEBINFO_PREVIEW_PATH));
		fp.setPreviewURL(AppParamUtil.getValue(WEBINFO_PREVIEW_URL));
		fp.setPublishPath(AppParamUtil.getValue(WEBINFO_PUBLISH_PATH));
		fp.setPublishURL(AppParamUtil.getValue(WEBINFO_PUBLISH_URL));
		return fp;
	}
	
}