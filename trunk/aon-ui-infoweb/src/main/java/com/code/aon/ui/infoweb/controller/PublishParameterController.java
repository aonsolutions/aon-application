package com.code.aon.ui.infoweb.controller;

import static com.code.aon.ui.infoweb.controller.IInfoWebConstants.FTP_PASSWORD_PARAM;
import static com.code.aon.ui.infoweb.controller.IInfoWebConstants.FTP_SERVER_PARAM;
import static com.code.aon.ui.infoweb.controller.IInfoWebConstants.FTP_USER_PARAM;
import static com.code.aon.ui.infoweb.controller.IInfoWebConstants.PREVIEW_PATH_PARAM;
import static com.code.aon.ui.infoweb.controller.IInfoWebConstants.PREVIEW_URL_PARAM;
import static com.code.aon.ui.infoweb.controller.IInfoWebConstants.PUBLISH_PATH_PARAM;
import static com.code.aon.ui.infoweb.controller.IInfoWebConstants.PUBLISH_URL_PARAM;
import static com.esferalia.aon.entity.IEntityAlias.APPLICATION_PARAMETER_NAME;

import java.util.List;

import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.ApplicationParameter;
import com.code.aon.config.util.AppParamUtil;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.infoweb.PublishProperties;


public class PublishParameterController {

	private static final Logger LOGGER = LoggerFactory.getLogger(PublishParameterController.class);
	
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
		AppParamUtil.insertParameter(FTP_SERVER_PARAM, to.getFtpServer());
		AppParamUtil.insertParameter(FTP_USER_PARAM, to.getFtpUser());
		AppParamUtil.insertParameter(FTP_PASSWORD_PARAM, to.getFtpPassword());
		AppParamUtil.insertParameter(PREVIEW_PATH_PARAM, to.getPreviewPath());
		AppParamUtil.insertParameter(PREVIEW_URL_PARAM, to.getPreviewURL());
		AppParamUtil.insertParameter(PUBLISH_PATH_PARAM, to.getPublishPath());
		AppParamUtil.insertParameter(PUBLISH_URL_PARAM, to.getPublishURL());
	}
	
	public PublishProperties getPublishProperties() {
		PublishProperties fp = new PublishProperties();
		fp.setFtpServer(AppParamUtil.getValue(FTP_SERVER_PARAM));
		fp.setFtpUser(AppParamUtil.getValue(FTP_USER_PARAM));
		fp.setFtpPassword(AppParamUtil.getValue(FTP_PASSWORD_PARAM));
		fp.setPreviewPath(AppParamUtil.getValue(PREVIEW_PATH_PARAM));
		fp.setPreviewURL(AppParamUtil.getValue(PREVIEW_URL_PARAM));
		fp.setPublishPath(AppParamUtil.getValue(PUBLISH_PATH_PARAM));
		fp.setPublishURL(AppParamUtil.getValue(PUBLISH_URL_PARAM));
		return fp;
	}
	
}