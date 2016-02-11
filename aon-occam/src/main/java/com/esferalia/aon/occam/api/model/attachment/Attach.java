package com.esferalia.aon.occam.api.model.attachment;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.type.MimeType;

@SuppressWarnings("serial")
public class Attach implements Serializable {
	
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
	private Boolean isDrive;
	
	//--------------------- Constructors
	
	public Attach() {

	}
	public Attach(AttachType attachType){
		this.attachType = attachType;
	}
	
	public Attach(Domain domain, Integer id, AttachType attachType, String name, MimeType mimetype, String driveId, byte[] data){
		this.domain = domain;
		this.id = id;
		this.attachType = attachType;
		this.description = name;
		this.mimeType = mimetype;
		this.driveId = driveId;
		this.data = data;
	}
	//--------------------- Getters & Setters
	
	public AttachType getAttachType() {
		return attachType;
	}

	public Attach setAttachType(AttachType attachType) {
		this.attachType = attachType;
		return this;
	}
	
	public byte[] getData() {
		return data;
	}

	public Attach setData(byte[] data) {
		this.data = data;
		return this;
	}
	public MimeType getMimeType() {
		return mimeType;
	}
	public Attach setMimeType(MimeType mimeType) {
		this.mimeType = mimeType;
		return this;
	}

	public short getType() {
		return type;
	}

	public Attach setType(short type) {
		this.type = type;
		return this;
	}

	public String getDriveId() {
		return driveId;
	}

	public Attach setDriveId(String driveId) {
		this.driveId = driveId;
		return this;
	}

	public Integer getId() {
		return id;
	}

	public Attach setId(Integer id) {
		this.id = id;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public Attach setDescription(String description) {
		this.description = description;
		return this;
	}

	public Date getDate() {
		return date;
	}

	public Attach setDate(Date date) {
		this.date = date;
		return this;
	}

	public String getIcon() {
		return icon;
	}

	public Attach setIcon(String icon) {
		this.icon = icon;
		return this;
	}

	public Boolean getConfidential() {
		return confidential;
	}

	public Attach setConfidential(Boolean confidential) {
		this.confidential = confidential;
		return this;
	}

	public Domain getDomain() {
		return domain;
	}

	public Attach setDomain(Domain domain) {
		this.domain = domain;
		return this;
	}

	public String getMd5() {
		return md5;
	}

	public Attach setMd5(String md5) {
		this.md5 = md5;
		return this;
	}
	
	public String getCreationUser() {
		return creationUser;
	}
	
	public Attach setCreationUser(String creationUser) {
		this.creationUser = creationUser;
		return this;
	}
	
	public Date getCreationDate() {
		return creationDate;
	}
	
	public Attach setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
		return this;
	}
	
	public Date getModificationDate() {
		return modificationDate;
	}
	
	public Attach setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
		return this;
	}
	
	public String getModificationUser() {
		return modificationUser;
	}
	
	public Attach setModificationUser(String modificationUser) {
		this.modificationUser = modificationUser;
		return this;
	}

	public Integer getCategory() {
		return category;
	}
	public Attach setCategory(Integer category) {
		this.category = category;
		return this;
	}
	public String getDparentId() {
		return dparentId;
	}
	public Attach setDparentId(String dparentId) {
		this.dparentId = dparentId;
		return this;
	}
	public Integer getScope() {
		return scope;
	}
	public Attach setScope(Integer scope) {
		this.scope = scope;
		return this;
	}
	public Integer getAttachModule() {
		return attachModule;
	}
	public Attach setAttachModule(Integer attachModule) {
		this.attachModule = attachModule;
		return this;
	}
	public Integer getSourceBatch() {
		return sourceBatch;
	}
	public Attach setSourceBatch(Integer sourceBatch) {
		this.sourceBatch = sourceBatch;
		return this;
	}
	public short getSourceType() {
		return sourceType;
	}
	public Attach setSourceType(short sourceType) {
		this.sourceType = sourceType;
		return this;
	}
	
	public Boolean getIsDrive() {
		return isDrive;
	}
	public Attach setIsDrive(Boolean isDrive) {
		this.isDrive = isDrive;
		return this;
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
