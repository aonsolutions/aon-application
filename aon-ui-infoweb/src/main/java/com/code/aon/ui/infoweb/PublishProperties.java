package com.code.aon.ui.infoweb;

import java.util.Properties;

import com.code.aon.ui.publisher.util.FTPUtil;

public class PublishProperties {

	private String ftpServer;
	
	private String ftpUser;
	
	private String ftpPassword;
	
	private String domain;

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
	
	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public Properties getFtpProperties() {
		Properties properties = new Properties();
		properties.put( FTPUtil.FTP_SERVER, ftpServer );
		properties.put( FTPUtil.FTP_USER, ftpUser );
		properties.put( FTPUtil.FTP_PASSWORD, ftpPassword );
		return properties;
	}
	
}
