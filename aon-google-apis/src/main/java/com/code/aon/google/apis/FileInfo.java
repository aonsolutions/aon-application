package com.code.aon.google.apis;

import java.io.InputStream;
import java.util.Vector;

public class FileInfo {
	
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
	
	
	

	private String aonType;
	private short type;
	private InputStream data;
	private String driveId;
	private int fileId;
	private String title;
	private short mimetype;
	private int category;
	private Vector<String> emails = new Vector<String>();
	private Boolean isNomina = false;

	public FileInfo() {
		// TODO Apéndice de constructor generado automáticamente
	}

	public FileInfo(String aonType, short type, InputStream data,
			String driveId, int fileId, String title, short mimetype) {
		// TODO Apéndice de constructor generado automáticamente
		this.type = type;
		this.data = data;
		this.driveId = driveId;
		this.fileId = fileId;
		this.title = title;
		this.mimetype = mimetype;
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

	public int getCategory() {
		return category;
	}

	public void setCategory(int category) {
		this.category = category;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public short getMimetype() {
		return mimetype;
	}

	public void setMimetype(short mimetype) {
		this.mimetype = mimetype;
	}

	public InputStream getData() {
		return data;
	}

	public void setData(InputStream data) {
		this.data = data;
	}

	public String getDriveId() {
		return driveId;
	}

	public void setDriveId(String driveId) {
		this.driveId = driveId;
	}

	public int getFileId() {
		return fileId;
	}

	public void setFileId(int fileId) {
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
	
}

