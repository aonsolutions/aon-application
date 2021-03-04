package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("serial")
public class DigitalCertificate implements Serializable {
	
	private Integer id;
	private Byte type;
	private Boolean confidential;
	private String password;
	private Boolean hasCertificate;
	private Date creationDate;
	private String description;
	
	public DigitalCertificate() {
		super();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Byte getType() {
		return type;
	}

	public void setType(Byte type) {
		this.type = type;
	}

	public Boolean getConfidential() {
		return null == confidential ? false : confidential;
	}

	public void setConfidential(Boolean confidential) {
		this.confidential = confidential;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Boolean getHasCertificate() {
		return hasCertificate;
	}

	public void setHasCertificate(Boolean hasCertificate) {
		this.hasCertificate = hasCertificate;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
}
