package com.esferalia.aon.occam.api.model.security;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.esferalia.aon.watson.util.AonStringUtils;

public class Certificate implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private Integer id;
	private Integer domain;
	private String type;
	private String password;
	private String name;
	private byte [] data;
	private boolean confidential;
	private boolean user;
	private List<CertificateType> types;
	private Date updateDate;

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
	
	public boolean hasPassword() {
		return !AonStringUtils.isBlank(getPassword());
	}

	@Deprecated
	public byte[] getCertificate() {
		return data;
	}
	
	@Deprecated
	public Certificate setCertificate(byte[] certificate) {
		this.data = certificate;
		return this;
	}
	
	public byte[] getData() {
		return data;
	}
	
	public Certificate setData(byte[] data) {
		this.data = data;
		return this;
	}
	
	public String getName() {
		return name;
	}
	
	public Certificate setName(String name) {
		this.name = name;
		return this;
	}
	
	public Certificate setDescription(String description) {
		if(description != null && description.contains("HIDE")) {
			String[] hide = description.split("HIDE\\(");
			String[] hidePass = hide[1].split("\\)");
			this.name = hide[0];
			this.password = hidePass.length > 0 ? hidePass[0] : "";
		} else {
			this.name = description != null ? description : "";
			this.password = "";
		}
		return this;
	}

	public boolean isConfidential() {
		return confidential;
	}
	
	public Certificate setConfidential(boolean confidential) {
		this.confidential = confidential;
		return this;
	}

	public boolean isUser() {
		return user;
	}
	
	public Certificate setUser(boolean user) {
		this.user = user;
		return this;
	}
	
	public boolean isCompany() {
		return !isUser();
	}

	public Certificate setCompany(boolean company) {
		setUser(!company);
		return this;
	}
	
	public Date getUpdateDate() {
		return updateDate;
	}

	public Certificate setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
		return this;
	}
	
	public boolean isEmpty() {
		return getData() == null && getPassword() == null;
	}
}
