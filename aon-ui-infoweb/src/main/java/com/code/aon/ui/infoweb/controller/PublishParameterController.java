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
import com.code.aon.ql.Criteria;
import com.code.aon.ui.infoweb.PublishProperties;


public class PublishParameterController {

	private static final Logger LOGGER = LoggerFactory.getLogger(PublishParameterController.class);
	
	private PublishProperties to;
	
	public static ApplicationParameter getParameter( String name ) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(ApplicationParameter.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(APPLICATION_PARAMETER_NAME), name);
			List<ITransferObject> list = bean.getList(criteria);
			if (! list.isEmpty() ) {
				return (ApplicationParameter) list.get(0);
			}
		} catch ( ManagerBeanException e ) {
			LOGGER.error( e.getMessage(), e );
		}
		return null;
	}
	
	public static ApplicationParameter insertParameter( String name, String value ) {
		try {
			ApplicationParameter ap = getParameter(name);
			if ( ap == null ) {
				ap = new ApplicationParameter();
				ap.setName(name);
			}
			IManagerBean bean = BeanManager.getManagerBean(ApplicationParameter.class);
			if ( StringUtils.isEmpty(value) ) {
				if ( ap.getId() != null ) {
					bean.remove(ap);
				}
			} else {
				ap.setValue(value);
				bean.insertOrUpdate( ap );
			}
			return ap;
		} catch (ManagerBeanException e) {
			LOGGER.error( e.getMessage(), e );
		}
		return null;
    }	

	private String getValue( String name ) {
		ApplicationParameter ap = getParameter(name);
		if ( ap != null ) {
			return StringUtils.trimToNull(ap.getValue());
		}
		return null;
	}
	
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
		insertParameter(FTP_SERVER_PARAM, to.getFtpServer());
		insertParameter(FTP_USER_PARAM, to.getFtpUser());
		insertParameter(FTP_PASSWORD_PARAM, to.getFtpPassword());
		insertParameter(PREVIEW_PATH_PARAM, to.getPreviewPath());
		insertParameter(PREVIEW_URL_PARAM, to.getPreviewURL());
		insertParameter(PUBLISH_PATH_PARAM, to.getPublishPath());
		insertParameter(PUBLISH_URL_PARAM, to.getPublishURL());
	}
	
	public PublishProperties getPublishProperties() {
		PublishProperties fp = new PublishProperties();
		fp.setFtpServer(getValue(FTP_SERVER_PARAM));
		fp.setFtpUser(getValue(FTP_USER_PARAM));
		fp.setFtpPassword(getValue(FTP_PASSWORD_PARAM));
		fp.setPreviewPath(getValue(PREVIEW_PATH_PARAM));
		fp.setPreviewURL(getValue(PREVIEW_URL_PARAM));
		fp.setPublishPath(getValue(PUBLISH_PATH_PARAM));
		fp.setPublishURL(getValue(PUBLISH_URL_PARAM));
		return fp;
	}
	
}