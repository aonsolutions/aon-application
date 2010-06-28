package com.code.aon.ui.publisher.controller;

import java.io.File;
import java.util.Properties;

import javax.faces.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.bridge.session.LoggedUser;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ui.publisher.util.FTPUtil;
import com.code.aon.ui.publisher.util.PathUtil;
import com.code.aon.ui.util.AonUtil;

public class PublisherController {

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
			LoggedUser loggedUser = (LoggedUser) AonUtil.getRegisteredBean(LoggedUser.LOGGED_USER);
			AuthPrincipal principal = loggedUser.getPrincipal();
			domain = PathUtil.getDomainSuffix(principal.getDomain());
		}
		return domain;
	}	

	public void onPublish(ActionEvent event) {
		try {
			File previewDirectory = PathUtil.getPreviewPath(getDomain());
			String destination = "/" + getDomain() + "/WEBSITES/www." + getDomain();
			FTPUtil.uploadFTP(previewDirectory, destination, properties);
			AonUtil.addInfoMessage("OK: La web ha sido publicada." );
		} catch (Throwable th) {
			LOGGER.error(th.getMessage(), th );
			AonUtil.addErrorMessage("ERROR: Se ha producido un error durante la publicacion de la pagina.");
		}		
	}		
}
