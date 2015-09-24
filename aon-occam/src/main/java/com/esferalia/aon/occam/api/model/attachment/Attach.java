package com.esferalia.aon.occam.api.model.attachment;

import java.util.Date;

import com.esferalia.aon.occam.api.model.type.MimeType;

public class Attach {
	
	AttachType attachType;
	
	byte[] data;
	MimeType mimeType;
	
	private short type;
	
	private String driveId;
	private Integer rattachId;
	private String description;
	
	private Date date;

	private String icon;
	private Boolean confidential;

	private String domainName;
	private Integer domainId;
	private String domainDescription;
	
	private String md5;
	
	private Integer project;
	
	public Attach() {

	}
	public Attach(AttachType attachType){
		this.attachType = attachType;
	}
	//--------------------- Getters & Setters
	
	public AttachType getAttachType() {
		return attachType;
	}

	public void setAttachType(AttachType attachType) {
		this.attachType = attachType;
	}
	
	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}
	public MimeType getMimeType() {
		return mimeType;
	}
	public void setMimeType(MimeType mimeType) {
		this.mimeType = mimeType;
	}

	public short getType() {
		return type;
	}

	public void setType(short type) {
		this.type = type;
	}

	public String getDriveId() {
		return driveId;
	}

	public void setDriveId(String driveId) {
		this.driveId = driveId;
	}

	public Integer getRattachId() {
		return rattachId;
	}

	public void setRattachId(Integer rattachId) {
		this.rattachId = rattachId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public Boolean getConfidential() {
		return confidential;
	}

	public void setConfidential(Boolean confidential) {
		this.confidential = confidential;
	}

	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public Integer getDomainId() {
		return domainId;
	}

	public void setDomainId(Integer domainId) {
		this.domainId = domainId;
	}

	public String getDomainDescription() {
		return domainDescription;
	}

	public void setDomainDescription(String domainDescription) {
		this.domainDescription = domainDescription;
	}

	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}

	public Integer getProject() {
		return project;
	}

	public void setProject(Integer project) {
		this.project = project;
	}
	
	
	
}
