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

	public void setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
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

	public void setAttachType(AttachType attachType) {
		this.attachType = attachType;
	}

	public Vector<String> getTags() {
		return tags;
	}

	public void setTags(Vector<String> tags) {
		this.tags = tags;
	}

	public String getAonType() {
		return aonType;
	}

	public void setAonType(String aonType) {
		this.aonType = aonType;
	}

	public short getType() {
		return type;
	}

	public void setType(short type) {
		this.type = type;
	}

	public Integer getCategory() {
		return category;
	}

	public void setCategory(Integer category) {
		this.category = category;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Byte getMimetype() {
		return mimetype;
	}

	public void setMimetype(Byte mimetype) {
		this.mimetype = mimetype;
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

	public void setDriveId(String driveId) {
		this.driveId = driveId;
	}

	public Integer getFileId() {
		return fileId;
	}

	public void setFileId(Integer fileId) {
		this.fileId = fileId;
	}

	public Vector<String> getEmails() {
		return emails;
	}

	public void setEmails(Vector<String> emails) {
		this.emails = emails;
	}

	public Boolean getIsNomina() {
		return isNomina;
	}

	public void setIsNomina(Boolean isNomina) {
		this.isNomina = isNomina;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public java.sql.Date getDateSql() {
		return dateSql;
	}

	public void setDateSql(java.sql.Date dateSql) {
		this.dateSql = dateSql;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public Integer getScopeId() {
		return scopeId;
	}

	public void setScopeId(Integer scopeId) {
		this.scopeId = scopeId;
	}

	public Byte getSecurityLevel() {
		return securityLevel;
	}

	public void setSecurityLevel(Byte securityLevel) {
		this.securityLevel = securityLevel;
	}

	public Integer getDomainId() {
		return domainId;
	}

	public void setDomainId(Integer domainId) {
		this.domainId = domainId;
	}

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}
	
}

