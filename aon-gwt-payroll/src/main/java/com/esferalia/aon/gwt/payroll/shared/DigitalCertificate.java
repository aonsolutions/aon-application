package com.esferalia.aon.gwt.payroll.shared;

import static com.esferalia.aon.gwt.payroll.shared.Shared.format;
import static com.esferalia.aon.gwt.payroll.shared.Shared.parse;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("serial")
public class DigitalCertificate implements Serializable {
	
	public enum CertificateType {
		SEPE,
		TGSS
	}
	
	private Integer rattachId;
	private Integer raddinfoId;
	private CertificateType type;
	private Boolean confidential;
	private String password;
	private Boolean hasCertificate;
	private String updateDate;
	private String description;
	
	public DigitalCertificate() {
		super();
	}

	public Integer getRattachId() {
		return rattachId;
	}

	public void setRattachId(Integer rattachId) {
		this.rattachId = rattachId;
	}

	public Integer getRaddinfoId() {
		return raddinfoId;
	}

	public void setRaddinfoId(Integer raddinfoId) {
		this.raddinfoId = raddinfoId;
	}

	public CertificateType getType() {
		return type;
	}

	public void setType(CertificateType certificateType) {
		this.type = certificateType;
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

	public Date getUpdateDate() {
		return parse(updateDate);
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = format(updateDate);
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
}
