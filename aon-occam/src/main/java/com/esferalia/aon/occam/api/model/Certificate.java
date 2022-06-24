package com.esferalia.aon.occam.api.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Certificate implements Serializable {
	
	// ----------------------------- Certificate enums
	
	public enum CertificateOwner {
		USER,
		ENTERPRISE
	}
	
	public enum CertificateSecurity {
		PUBLIC,
		PRIVATE
	}
	
	public enum CertificateType {
		TGSS,
		SEPE,
		AEAT
	}
	
	// ----------------------------- Variables

	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private Integer domain;
	private CertificateOwner owner;
	private String description;
	private CertificateSecurity confidential;
	private Boolean hasCertificate;
	private Date updateDate;
	private byte[] data;
	private String type;
	
	private Integer passwordId;
	private String password;
	
	private List<CertificateType> tags;
	
	private CertificateInfo certificateInfo;
	
	// ----------------------------- Constructor
	
	public Certificate() {
		super();
	}

	// ----------------------------- Getter / Setter
	
	public Integer getId() {
		return id;
	}

	public Certificate setId(Integer id) {
		this.id = id;
		return this;
	}
	
	public Integer getDomain() {
		return domain;
	}
	
	public Certificate setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}

	public CertificateOwner getOwner() {
		return owner;
	}

	public Certificate setOwner(CertificateOwner owner) {
		this.owner = owner;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public Certificate setDescription(String description) {
		this.description = description;
		return this;
	}

	public CertificateSecurity getConfidential() {
		return confidential;
	}

	public Certificate setConfidential(CertificateSecurity confidential) {
		this.confidential = confidential;
		return this;
	}

	public Boolean getHasCertificate() {
		return hasCertificate;
	}

	public Certificate setHasCertificate(Boolean hasCertificate) {
		this.hasCertificate = hasCertificate;
		return this;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public Certificate setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
		return this;
	}

	public byte[] getData() {
		return data;
	}

	public Certificate setData(byte[] data) {
		this.data = data;
		return this;
	}

	public String getType() {
		return type;
	}

	public Certificate setType(String type) {
		this.type = type;
		return this;
	}

	public Integer getPasswordId() {
		return passwordId;
	}

	public Certificate setPasswordId(Integer passwordId) {
		this.passwordId = passwordId;
		return this;
	}

	public String getPassword() {
		return password;
	}

	public Certificate setPassword(String password) {
		this.password = password;
		return this;
	}

	public List<CertificateType> getTags() {
		return tags;
	}

	public Certificate setTags(List<CertificateType> tags) {
		this.tags = tags;
		return this;
	}
	
	public void addTag(CertificateType tag) {
		if(this.tags == null)
			this.tags = new ArrayList<>();
		this.tags.add(tag);
	}

	public void removeTag(CertificateType tag) {
		if(this.tags != null)
			this.tags.remove(tag);
	}

	public CertificateInfo getCertificateInfo() {
		if(certificateInfo == null) {
			certificateInfo = new CertificateInfo();
		}
		return certificateInfo;
	}

	public Certificate setCertificateInfo(CertificateInfo certificateInfo) {
		this.certificateInfo = certificateInfo;
		return this;
	}
	
}
