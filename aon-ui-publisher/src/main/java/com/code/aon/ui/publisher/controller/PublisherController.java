package com.code.aon.ui.publisher.controller;

import static com.code.aon.ui.common.ICommonMessages.DELETE_ERROR;
import static com.code.aon.ui.common.ICommonMessages.DELETE_OK;
import static com.code.aon.ui.common.ICommonMessages.PUBLISH_ERROR;
import static com.code.aon.ui.common.ICommonMessages.PUBLISH_OK;

import java.io.File;
import java.io.Serializable;
import java.util.Properties;

import javax.faces.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.DefaultLogger;
import com.code.aon.common.ILogger;
import com.code.aon.faces.controller.LogPanelController;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ui.config.util.FTPUtil;
import com.code.aon.ui.publisher.util.PathUtil;
import com.code.aon.ui.util.AonUtil;

public class PublisherController implements IPublisherConstants, Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private static final Logger LOGGER = LoggerFactory.getLogger(PublisherController.class.getName());
	
	private static final String DEFAULT_FTP_PROPERTIES = "/com/code/aon/ui/publisher/ftp.default.properties";

	private String domain;
	
	private Properties properties;

	private String previewPage;
	
	private String webPage;
	
	public PublisherController() {
		this.properties = FTPUtil.getProperties(PathUtil.getPublisherProperties(), DEFAULT_FTP_PROPERTIES);
		this.previewPage = "http://preview." + getDomain() + "/";
		this.webPage = "http://www." + getDomain() + "/";
	}
	
	public String getWebPage() {
		return webPage;
	}
	
	public String getPreviewPage() {
		return previewPage;
	}
	
	public File getPreviewPath() {
		return PathUtil.getPreviewPath(getDomain());
	}
	
	private String getDomain() {
		if ( domain == null ) {
			AuthPrincipal principal = AonUtil.getAuthPrincipal();
			domain = PathUtil.getDomainSuffix(principal.getDomain());
		}
		return domain;
	}	
	
	private String getDestination() {
		return "/" + getDomain() + "/WEBSITES/www." + getDomain();
	}

	public void onPublish(ActionEvent event) {
		boolean published = false;
		LogPanelController log = LogPanelController.getInstance();
		FTPUtil ftp = new FTPUtil(log);
		try {
			File previewDirectory = PathUtil.getPreviewPath(getDomain());
			ftp.connect(properties);
			if ( ftp.isConnected() ) {
				ftp.synchronize(previewDirectory, getDestination());
				published = true;
			}
		} catch (Throwable th) {
			LOGGER.error(th.getMessage(), th );
			log.error( AonUtil.getMessage(PUBLISH_ERROR) );
		} finally {
			ftp.close();
		}
		if ( published ) {
			log.info( AonUtil.getMessage(PUBLISH_OK) );
		}
		log.finish();
	}		

	public void onDelete(ActionEvent event) {
		ILogger log = new DefaultLogger(LOGGER);
		FTPUtil ftp = new FTPUtil(log);
		try {
			ftp.connect(properties);
			if ( ftp.isConnected() ) {
				ftp.delete(getDestination());
				AonUtil.addInfoMessageFromBundle(DELETE_OK);
				return;
			}
		} catch (Throwable th) {
			LOGGER.error(th.getMessage(), th );
		} finally {
			ftp.close();
		}
		log.error( AonUtil.getMessage(DELETE_ERROR) );		
	}		
	
}