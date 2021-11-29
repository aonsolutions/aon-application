package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.esferalia.aon.watson.util.AonStringUtils;

@SuppressWarnings("serial")
public class DigitalCertificateNew implements Serializable {
	
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
	
	private Integer rattachId;
	private CertificateOwner owner;
	private String description;
	private CertificateSecurity confidential;
	private Boolean hasCertificate;
	private Date updateDate;
	
	private Integer raddinfoId;
	private String password;
	
	private List<CertificateType> tags;
	
	public DigitalCertificateNew() {
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

	public CertificateOwner getOwner() {
		return owner;
	}

	public void setOwner(CertificateOwner owner) {
		this.owner = owner;
	}

	public List<CertificateType> getTags() {
		return tags;
	}

	public void setTags(List<CertificateType> tags) {
		this.tags = tags;
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

	public CertificateSecurity getConfidential() {
		return confidential;
	}

	public void setConfidential(CertificateSecurity confidential) {
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
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public String getDescription() {
		if(AonStringUtils.isBlank(description))
			return null;
		
		String[] descriptionSplit = description.split("HIDE");
		return descriptionSplit.length > 1 ? descriptionSplit[0] : description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "DigitalCertificateNew [rattachId=" + rattachId + ", owner=" + owner + ", description=" + description
				+ ", confidential=" + confidential + ", hasCertificate=" + hasCertificate + ", updateDate=" + updateDate
				+ ", raddinfoId=" + raddinfoId + ", password=" + password + ", tags=" + tags + "]\n";
	}
	
	
}
