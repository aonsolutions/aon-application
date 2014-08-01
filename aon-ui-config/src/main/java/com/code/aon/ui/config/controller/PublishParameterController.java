package com.code.aon.ui.config.controller;

import java.io.Serializable;

import javax.faces.event.ActionEvent;

import com.code.aon.AonVersion;
import com.code.aon.common.enumeration.AppParam;
import com.code.aon.config.util.AppParamUtil;
import com.code.aon.ui.config.PublishProperties;

public class PublishParameterController implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private PublishProperties to;
	
	public void onInit( ActionEvent event ) {
		this.to = getPublishProperties();
	}

	public PublishProperties getTo() {
		return to;
	}

	public void setTo(PublishProperties to) {
		this.to = to;
	}

	public void accept(ActionEvent event) {
		AppParamUtil.insertParameter(AppParam.WEBINFO_FTP_SERVER, to.getFtpServer());
		AppParamUtil.insertParameter(AppParam.WEBINFO_FTP_USER, to.getFtpUser());
		AppParamUtil.insertParameter(AppParam.WEBINFO_FTP_PASSWORD, to.getFtpPassword());
		AppParamUtil.insertParameter(AppParam.WEBINFO_PREVIEW_PATH, to.getPreviewPath());
		AppParamUtil.insertParameter(AppParam.WEBINFO_PREVIEW_URL, to.getPreviewURL());
		AppParamUtil.insertParameter(AppParam.WEBINFO_PUBLISH_PATH, to.getPublishPath());
		AppParamUtil.insertParameter(AppParam.WEBINFO_PUBLISH_URL, to.getPublishURL());
	}
	
	public PublishProperties getPublishProperties() {
		PublishProperties fp = new PublishProperties();
		fp.setFtpServer(AppParamUtil.getValue(AppParam.WEBINFO_FTP_SERVER));
		fp.setFtpUser(AppParamUtil.getValue(AppParam.WEBINFO_FTP_USER));
		fp.setFtpPassword(AppParamUtil.getValue(AppParam.WEBINFO_FTP_PASSWORD));
		fp.setPreviewPath(AppParamUtil.getValue(AppParam.WEBINFO_PREVIEW_PATH));
		fp.setPreviewURL(AppParamUtil.getValue(AppParam.WEBINFO_PREVIEW_URL));
		fp.setPublishPath(AppParamUtil.getValue(AppParam.WEBINFO_PUBLISH_PATH));
		fp.setPublishURL(AppParamUtil.getValue(AppParam.WEBINFO_PUBLISH_URL));
		return fp;
	}
	
}