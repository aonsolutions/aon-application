package net.aonsolutions.occam.api.model;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.occam.api.model.type.CertificateType;

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
	
	// ----------------------------- Variables

	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private Integer domain;
	private CertificateOwner owner;
	private String description;
	private CertificateSecurity confidential;
	private Date updateDate;
	private byte[] data;
	private String type;
	
	private String password;
	
	private LinkedList<CertificateType> tags;
	
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
		if(description != null && description.contains("HIDE")) {
			String[] hide = description.split("HIDE\\(");
			String[] hidePass = hide[1].split("\\)");
			this.description = hide[0];
			this.password = hidePass.length > 0 ? hidePass[0] : "";
		} else {
			this.description = description != null ? description : "";
			this.password = "";
		}
		return this;
	}

	public CertificateSecurity getConfidential() {
		if(confidential == null) {
			confidential = CertificateSecurity.PUBLIC;
		}
		return confidential;
	}
	
	public boolean isConfidential() {
		return CertificateSecurity.PRIVATE.equals(getConfidential());
	}

	public Certificate setConfidential(CertificateSecurity confidential) {
		this.confidential = confidential;
		return this;
	}
	
	public Certificate setConfidential(boolean confidential) {
		this.confidential = confidential 
			? CertificateSecurity.PRIVATE
			: CertificateSecurity.PUBLIC;				
		return this;
	}

	public boolean hasPassword() {
		return !AonStringUtils.isBlank(getPassword());
	}
	
	public boolean hasCertificate() {
		return getData() != null;
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

	public Certificate setTags(LinkedList<CertificateType> tags) {
		this.tags = tags;
		return this;
	}
	
	public void addTag(CertificateType tag) {
		if(this.tags == null)
			this.tags = new LinkedList<>();
		this.tags.add(tag);
	}

	public void removeTag(CertificateType tag) {
		if(this.tags != null)
			this.tags.remove(tag);
	}

	public CertificateInfo getCertificateInfo() {
		return certificateInfo;
	}

	public Certificate setCertificateInfo(CertificateInfo certificateInfo) {
		this.certificateInfo = certificateInfo;
		return this;
	}
	
	public boolean isEmpty() {
		return getData() == null && getPassword() == null;
	}
}
