package com.esferalia.aon.gwt.dump.shared;

import java.io.Serializable;

public class Parameters implements Serializable {

	private String newDomain;
	private String newUserName;
	private String newUserPass;
	private String descripcionEmpresa;
	private Boolean comments;
	private Boolean fk;
	private Boolean eraseUsers;
	private Boolean locks;
	private Integer downloadType;
	
	public String getNewDomain() {
		return newDomain;
	}
	public Parameters setNewDomain(String newDomain) {
		this.newDomain = newDomain;
		return this;
	}
	public String getNewUserName() {
		return newUserName;
	}
	public Parameters setNewUserName(String newUserName) {
		this.newUserName = newUserName;
		return this;
	}
	public String getNewUserPass() {
		return newUserPass;
	}
	public Parameters setNewUserPass(String newUserPass) {
		this.newUserPass = newUserPass;
		return this;
	}
	public Boolean getComments() {
		return comments;
	}
	public Parameters setComments(Boolean comments) {
		this.comments = comments;
		return this;
	}
	public Boolean getFk() {
		return fk;
	}
	public Parameters setFk(Boolean fk) {
		this.fk = fk;
		return this;
	}
	public Boolean getEraseUsers() {
		return eraseUsers;
	}
	public Parameters setEraseUsers(Boolean eraseUsers) {
		this.eraseUsers = eraseUsers;
		return this;
	}
	public Boolean getLocks() {
		return locks;
	}
	public Parameters setLocks(Boolean locks) {
		this.locks = locks;
		return this;
	}
	public Integer getDownloadType() {
		return downloadType;
	}
	public Parameters setDownloadType(Integer downloadType) {
		this.downloadType = downloadType;
		return this;
	}
	public String getDescripcionEmpresa() {
		return descripcionEmpresa;
	}
	public Parameters setDescripcionEmpresa(String descripcionEmpresa) {
		this.descripcionEmpresa = descripcionEmpresa;
		return this;
	}
	
}
