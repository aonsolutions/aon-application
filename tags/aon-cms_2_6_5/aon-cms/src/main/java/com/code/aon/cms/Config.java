package com.code.aon.cms;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.aon.common.ITransferObject;

@Entity
@Table(name = "config")
public class Config implements ITransferObject {

	private Integer id;

	private boolean online;

	private String domain;

	private String preview_host;

	private String host;

	private String ftp_server;

	private String ftp_user;

	private String ftp_password;
	
	private String ftp_path;

	private String smtp_server;

	private boolean smtp_auth;
	
	private String smtp_user;

	private String smtp_password;

	private String from_name;

	private String from_email;

	private String template;
	
	private String preview_ftp_server;

	private String preview_ftp_user;

	private String preview_ftp_password;
	
	private String preview_ftp_path;

	private Set<ConfigDetail> details;

	@Id
	@Column(name = "id", nullable = false)
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "online", nullable = false)
	public boolean isOnline() {
		return online;
	}

	public void setOnline(boolean online) {
		this.online = online;
	}

	@Column(name = "domain", length = 128)
	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	@Column(name = "preview_host", length = 24)
	public String getPreview_host() {
		return preview_host;
	}

	public void setPreview_host(String preview_host) {
		this.preview_host = preview_host;
	}

	@Column(name = "host", length = 24)
	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	@Column(name = "ftp_server", length = 128)
	public String getFtp_server() {
		return ftp_server;
	}

	public void setFtp_server(String ftp_server) {
		this.ftp_server = ftp_server;
	}

	@Column(name = "ftp_user", length = 32)
	public String getFtp_user() {
		return ftp_user;
	}

	public void setFtp_user(String ftp_user) {
		this.ftp_user = ftp_user;
	}

	@Column(name = "ftp_password", length = 32)
	public String getFtp_password() {
		return ftp_password;
	}

	public void setFtp_password(String ftp_password) {
		this.ftp_password = ftp_password;
	}

	@Column(name = "ftp_path", length = 255)
	public String getFtp_path() {
		return ftp_path;
	}

	public void setFtp_path(String ftp_path) {
		this.ftp_path = ftp_path;
	}

	@Column(name = "smtp_server", length = 128)
	public String getSmtp_server() {
		return smtp_server;
	}

	public void setSmtp_server(String smtp_server) {
		this.smtp_server = smtp_server;
	}

	@Column(name = "smtp_auth")
	public boolean isSmtp_auth() {
		return smtp_auth;
	}

	public void setSmtp_auth(boolean smtp_auth) {
		this.smtp_auth = smtp_auth;
	}

	@Column(name = "smtp_user", length = 32)
	public String getSmtp_user() {
		return smtp_user;
	}

	public void setSmtp_user(String smtp_user) {
		this.smtp_user = smtp_user;
	}

	@Column(name = "smtp_password", length = 32)
	public String getSmtp_password() {
		return smtp_password;
	}

	public void setSmtp_password(String smtp_password) {
		this.smtp_password = smtp_password;
	}

	@Column(name = "from_name", length = 64)
	public String getFrom_name() {
		return from_name;
	}

	public void setFrom_name(String from_name) {
		this.from_name = from_name;
	}

	@Column(name = "from_email", length = 128)
	public String getFrom_email() {
		return from_email;
	}

	public void setFrom_email(String from_email) {
		this.from_email = from_email;
	}

	@Column(name = "template", length = 32)
	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}
	
	@Transient
	public String getPreviewUrl() {
		return this.preview_host + "." + this.domain;
	}

	@Transient
	public String getPublisUrl() {
		return this.host + "." + this.domain;
	}

	@OneToMany(mappedBy = "config", cascade={CascadeType.REMOVE})
	public Set<ConfigDetail> getDetails() {
		return this.details;
	}

	public void setDetails( Set<ConfigDetail> details ) {
		this.details = details;
	}

	@Column(name = "preview_ftp_server", length = 128)
	public String getPreview_ftp_server() {
		return preview_ftp_server;
	}

	public void setPreview_ftp_server(String preview_ftp_server) {
		this.preview_ftp_server = preview_ftp_server;
	}

	@Column(name = "preview_ftp_user", length = 32)
	public String getPreview_ftp_user() {
		return preview_ftp_user;
	}

	public void setPreview_ftp_user(String preview_ftp_user) {
		this.preview_ftp_user = preview_ftp_user;
	}

	@Column(name = "preview_ftp_password", length = 32)
	public String getPreview_ftp_password() {
		return preview_ftp_password;
	}

	public void setPreview_ftp_password(String preview_ftp_password) {
		this.preview_ftp_password = preview_ftp_password;
	}

	@Column(name = "preview_ftp_path", length = 255)
	public String getPreview_ftp_path() {
		return preview_ftp_path;
	}

	public void setPreview_ftp_path(String preview_ftp_path) {
		this.preview_ftp_path = preview_ftp_path;
	}

}
