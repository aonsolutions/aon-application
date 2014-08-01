package com.code.aon.ui.config;

import java.io.Serializable;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.ui.config.util.FTPUtil;

public class PublishProperties implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private String ftpServer;
	
	private String ftpUser;
	
	private String ftpPassword;
	
	private String previewPath;
	
	private String previewURL;
	
	private String publishPath;
	
	private String publishURL;

	public String getFtpServer() {
		return ftpServer;
	}

	public void setFtpServer(String ftpServer) {
		this.ftpServer = ftpServer;
	}

	public String getFtpUser() {
		return ftpUser;
	}

	public void setFtpUser(String ftpUser) {
		this.ftpUser = ftpUser;
	}

	public String getFtpPassword() {
		return ftpPassword;
	}

	public void setFtpPassword(String ftpPassword) {
		this.ftpPassword = ftpPassword;
	}
	
	public String getPreviewPath() {
		return previewPath;
	}

	public void setPreviewPath(String previewPath) {
		this.previewPath = previewPath;
	}

	public String getPreviewURL() {
		return previewURL;
	}

	public void setPreviewURL(String previewURL) {
		this.previewURL = previewURL;
	}

	public String getPublishPath() {
		return publishPath;
	}

	public void setPublishPath(String publishPath) {
		this.publishPath = publishPath;
	}

	public String getPublishURL() {
		return publishURL;
	}

	public void setPublishURL(String publishURL) {
		this.publishURL = publishURL;
	}

	public boolean isEmpty() {
		return StringUtils.isEmpty(ftpServer) || StringUtils.isEmpty(ftpUser) || StringUtils.isEmpty(ftpPassword)
				|| StringUtils.isEmpty(previewPath) || StringUtils.isEmpty(previewURL)
				|| StringUtils.isEmpty(publishPath) || StringUtils.isEmpty(publishURL);
	}
	
	public Properties getFtpProperties() {
		Properties properties = new Properties();
		properties.put( FTPUtil.FTP_SERVER, ftpServer );
		properties.put( FTPUtil.FTP_USER, ftpUser );
		properties.put( FTPUtil.FTP_PASSWORD, ftpPassword );
		return properties;
	}
	
}
