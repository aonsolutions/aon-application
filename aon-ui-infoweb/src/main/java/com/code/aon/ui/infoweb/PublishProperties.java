package com.code.aon.ui.infoweb;

import java.util.Properties;

import com.code.aon.ui.publisher.util.FTPUtil;

public class PublishProperties {

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

	public Properties getFtpProperties() {
		Properties properties = new Properties();
		properties.put( FTPUtil.FTP_SERVER, ftpServer );
		properties.put( FTPUtil.FTP_USER, ftpUser );
		properties.put( FTPUtil.FTP_PASSWORD, ftpPassword );
		return properties;
	}
	
}
