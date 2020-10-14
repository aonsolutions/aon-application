package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.Date;

public class ContractAttach implements Serializable {
	
	private Integer id;
	private Integer domain;
	private Integer contract;
	private Byte mimeType;
	private String description;
	private byte[] data;
	private Byte type;
	private Integer scope;
	private Byte securityLevel;
	private Date attachDate;
	private String driveId;
	
	public ContractAttach() {
		super();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getDomain() {
		return domain;
	}

	public void setDomain(Integer domain) {
		this.domain = domain;
	}

	public Integer getContract() {
		return contract;
	}

	public void setContract(Integer contract) {
		this.contract = contract;
	}

	public Byte getMimeType() {
		return mimeType;
	}

	public void setMimeType(Byte mimeType) {
		this.mimeType = mimeType;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public Byte getType() {
		return null == type ? (byte) -1 : type;
	}

	public void setType(Byte type) {
		this.type = type;
	}

	public Integer getScope() {
		return null == scope ? (byte) -1 : scope;
	}

	public void setScope(Integer scope) {
		this.scope = scope;
	}

	public Byte getSecurityLevel() {
		return securityLevel;
	}

	public void setSecurityLevel(Byte securityLevel) {
		this.securityLevel = securityLevel;
	}

	public Date getAttachDate() {
		return attachDate;
	}

	public void setAttachDate(Date attachDate) {
		this.attachDate = attachDate;
	}

	public String getDriveId() {
		return driveId;
	}

	public void setDriveId(String driveId) {
		this.driveId = driveId;
	}
	
}
