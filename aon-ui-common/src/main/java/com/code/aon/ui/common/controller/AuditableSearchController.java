package com.code.aon.ui.common.controller;

import java.io.Serializable;
import java.util.Date;

import com.code.aon.AonVersion;

public class AuditableSearchController implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private String creationUser;
	private Date creationDate1;
	private Date creationDate2;
	private String modificationUser;
	private Date modificationDate1;
	private Date modificationDate2;
	
	public String getCreationUser() {
		return creationUser;
	}
	
	public void setCreationUser(String creationUser) {
		this.creationUser = creationUser;
	}
	
	public Date getCreationDate1() {
		return creationDate1;
	}

	public void setCreationDate1(Date creationDate1) {
		this.creationDate1 = creationDate1;
	}

	public Date getCreationDate2() {
		return creationDate2;
	}

	public void setCreationDate2(Date creationDate2) {
		this.creationDate2 = creationDate2;
	}

	public String getModificationUser() {
		return modificationUser;
	}

	public void setModificationUser(String modificationUser) {
		this.modificationUser = modificationUser;
	}

	public Date getModificationDate1() {
		return modificationDate1;
	}

	public void setModificationDate1(Date modificationDate1) {
		this.modificationDate1 = modificationDate1;
	}

	public Date getModificationDate2() {
		return modificationDate2;
	}

	public void setModificationDate2(Date modificationDate2) {
		this.modificationDate2 = modificationDate2;
	}

}
