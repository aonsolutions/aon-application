package com.esferalia.aon.occam.api.model.attachment;

import java.util.Date;

import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.type.MimeType;

public class Attach {
	
	AttachType attachType;
	private Integer attachModule;
	
	//---------- Generic
	private Integer id;
	private Domain domain;
	private MimeType mimeType;
	private String description;
	private byte[] data;
	private Date date;
	private short type;
	private String driveId;
	private Integer scope;
	private Boolean confidential;

	//---------- Registry
	private Integer category;
	private String dparentId;
	
	//---------- Sepe & Payroll
	private Integer sourceBatch;
	private short sourceType;
	
	//---------- Audit
	private String creationUser;
	private Date creationDate;
	private String modificationUser;
	private Date modificationDate;
	
	//---------- Auxiliar
	private String icon;
	private String md5;
	
	//--------------------- Constructors
	
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

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	public Domain getDomain() {
		return domain;
	}

	public void setDomain(Domain domain) {
		this.domain = domain;
	}

	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}
	
	public String getCreationUser() {
		return creationUser;
	}
	
	public void setCreationUser(String creationUser) {
		this.creationUser = creationUser;
	}
	
	public Date getCreationDate() {
		return creationDate;
	}
	
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	
	public Date getModificationDate() {
		return modificationDate;
	}
	
	public void setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
	}
	
	public String getModificationUser() {
		return modificationUser;
	}
	
	public void setModificationUser(String modificationUser) {
		this.modificationUser = modificationUser;
	}

	public Integer getCategory() {
		return category;
	}
	public void setCategory(Integer category) {
		this.category = category;
	}
	public String getDparentId() {
		return dparentId;
	}
	public void setDparentId(String dparentId) {
		this.dparentId = dparentId;
	}
	public Integer getScope() {
		return scope;
	}
	public void setScope(Integer scope) {
		this.scope = scope;
	}
	public Integer getAttachModule() {
		return attachModule;
	}
	public void setAttachModule(Integer attachModule) {
		this.attachModule = attachModule;
	}
	public Integer getSourceBatch() {
		return sourceBatch;
	}
	public void setSourceBatch(Integer sourceBatch) {
		this.sourceBatch = sourceBatch;
	}
	public short getSourceType() {
		return sourceType;
	}
	public void setSourceType(short sourceType) {
		this.sourceType = sourceType;
	}
	
	public static Attach projectAttach(Integer project, Domain domain, com.esferalia.aon.occam.api.model.type.MimeType mimetype, String description,
			byte[] data, Boolean confidential, Date date, String driveId){
		Attach attach = new Attach(AttachType.PROJECT);
		attach.setAttachModule(project);
		attach.setDomain(domain);
		attach.setMimeType(mimetype);
		attach.setDescription(description);
		attach.setData(data);
		attach.setConfidential(confidential);
		attach.setDate(date);
		attach.setDriveId(driveId);

		return attach;
	}
}
