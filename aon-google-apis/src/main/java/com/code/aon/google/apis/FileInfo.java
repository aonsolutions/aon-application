package com.code.aon.google.apis;

import java.util.Date;
import java.util.Vector;

import com.esferalia.aon.occam.api.model.attachment.AttachType;

public class FileInfo{
	
	static enum AonType {
		REGISTRY{
			@Override
			<T,R> T visit(AonTypeVisitor<T,R> visitor, R r) {
				return visitor.visitRegistry(this,r);
			}
		},
		PAYROOL{
		 @Override
		 	<T,R> T visit(AonTypeVisitor<T,R> visitor, R r) {
			 return visitor.visitPayroll(this,r);
		 	}	
		}
		,SEPE{
			@Override
			<T,R> T visit(AonTypeVisitor<T,R> visitor, R r) {
				return visitor.visitSepe(this,r);
			}
		};
		
		abstract <T,R> T visit(AonTypeVisitor<T,R> visitor, R r);
	}
	
	static interface AonTypeVisitor<T,R>{
		T visitRegistry(AonType type, R r);
		T visitSepe(AonType type, R r);
		T visitPayroll(AonType type, R r);
	}
	
	
	private AttachType attachType;
	private String aonType;
	private short type;
	private byte [] data;
	private String driveId;
	private Integer fileId;
	private String title;
	private Byte mimetype;
	private Integer category;
	private Vector<String> emails = new Vector<String>();
	private Boolean isNomina = false;
	private Date date;
	private java.sql.Date dateSql;
	private String tag;
	private Integer scopeId;
	private Byte securityLevel;
	private Vector<String> tags;
	private String domain;
	private Integer domainId;
	private Integer size;
	
	private Date modificationDate;

	
	
	
	public Date getModificationDate() {
		return modificationDate;
	}

	public FileInfo setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
		return this;
	}

	public FileInfo() {
		// TODO Apéndice de constructor generado automáticamente
	}

	public FileInfo(String aonType, short type, byte [] data,
			String driveId, Integer fileId, String title, Byte mimetype) {
		// TODO Apéndice de constructor generado automáticamente
		this.type = type;
		this.data = data;
		this.driveId = driveId;
		this.fileId = fileId;
		this.title = title;
		this.mimetype = mimetype;
	}
	
	

	public AttachType getAttachType() {
		return attachType;
	}

	public FileInfo setAttachType(AttachType attachType) {
		this.attachType = attachType;
		return this;
	}

	public Vector<String> getTags() {
		return tags;
	}

	public FileInfo setTags(Vector<String> tags) {
		this.tags = tags;
		return this;
	}

	public String getAonType() {
		return aonType;
	}

	public FileInfo setAonType(String aonType) {
		this.aonType = aonType;
		return this;
	}

	public short getType() {
		return type;
	}

	public FileInfo setType(short type) {
		this.type = type;
		return this;
	}

	public Integer getCategory() {
		return category;
	}

	public FileInfo setCategory(Integer category) {
		this.category = category;
		return this;
	}

	public String getTitle() {
		return title;
	}

	public FileInfo setTitle(String title) {
		this.title = title;
		return this;
	}

	public Byte getMimetype() {
		return mimetype;
	}

	public FileInfo setMimetype(Byte mimetype) {
		this.mimetype = mimetype;
		return this;
	}

	public byte[] getData() {
		return data;
	}

	public FileInfo setData(byte[] data) {
		this.data = data;
		return this;
	}

	public String getDriveId() {
		return driveId;
	}

	public FileInfo setDriveId(String driveId) {
		this.driveId = driveId;
		return this;
	}

	public Integer getFileId() {
		return fileId;
	}

	public FileInfo setFileId(Integer fileId) {
		this.fileId = fileId;
		return this;
	}

	public Vector<String> getEmails() {
		return emails;
	}

	public FileInfo setEmails(Vector<String> emails) {
		this.emails = emails;
		return this;
	}

	public Boolean getIsNomina() {
		return isNomina;
	}

	public FileInfo setIsNomina(Boolean isNomina) {
		this.isNomina = isNomina;
		return this;
	}

	public Date getDate() {
		return date;
	}

	public FileInfo setDate(Date date) {
		this.date = date;
		return this;
	}

	public java.sql.Date getDateSql() {
		return dateSql;
	}

	public FileInfo setDateSql(java.sql.Date dateSql) {
		this.dateSql = dateSql;
		return this;
	}

	public String getTag() {
		return tag;
	}

	public FileInfo setTag(String tag) {
		this.tag = tag;
		return this;
	}

	public Integer getScopeId() {
		return scopeId;
	}

	public FileInfo setScopeId(Integer scopeId) {
		this.scopeId = scopeId;
		return this;
	}

	public Byte getSecurityLevel() {
		return securityLevel;
	}

	public FileInfo setSecurityLevel(Byte securityLevel) {
		this.securityLevel = securityLevel;
		return this;
	}

	public Integer getDomainId() {
		return domainId;
	}

	public FileInfo setDomainId(Integer domainId) {
		this.domainId = domainId;
		return this;
	}

	public Integer getSize() {
		return size;
	}

	public FileInfo setSize(Integer size) {
		this.size = size;
		return this;
	}

	public String getDomain() {
		return domain;
	}

	public FileInfo setDomain(String domain) {
		this.domain = domain;
		return this;
	}
	
}

