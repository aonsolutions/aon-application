package com.esferalia.aon.gwt.common.shared;


import java.util.Date;
import java.util.Vector;

import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.google.gwt.user.client.rpc.IsSerializable;
import com.google.gwt.view.client.ProvidesKey;



	public class FileInfo implements IsSerializable{
		
		public static final ProvidesKey<FileInfo> PROVIDES_KEY = new ProvidesKey<FileInfo>() {
			@Override
			public Object getKey(FileInfo fileInfo) {
				return fileInfo == null ? null : fileInfo.getFileId();
			}
		};
		
		private String aonType;
		private short type;
		
		private String driveId;
		private Integer fileId;
		private String title;
		private Byte mimetype;
		private Integer category;
		private Vector<String> emails= new Vector<String>();
		
		private Boolean isGdocs = false;
		private Boolean isDrive = false;
		private Boolean isParent = false;
		private Boolean isNomina = false;
		//private Vector<Tag> tags;
		private String tagsStr;
		private Date date;
		private java.sql.Date dateSql;
		private String dateStr;

		private Integer size;
		private String sizeStr;
		private String categoryStr;
		
		private String icon;
		private Boolean confidential;
		private Byte conf;
		//private Scope scope;
		
		private String domain;
		private Integer domainId;
		private String domainDescription;
		private byte[] data;
		private String mimeString;
		
		private String md5;
		
		public FileInfo() {
			// TODO Apéndice de constructor generado automáticamente
		}
		
		public FileInfo(String aonType,Byte type,String driveId, int fileId , String title,Byte mimetype) {
			// TODO Apéndice de constructor generado automáticamente
			this.type=type;
			this.driveId=driveId;
			this.fileId=fileId;
			this.title=title;
			this.mimetype=mimetype;
		}
		
		public FileInfo(Attach rattach) {
			this.data = rattach.getData();
			this.mimeString = rattach.getMimeType().getName();
			this.mimetype = (byte) rattach.getMimeType().ordinal();
			this.type = rattach.getType();
			this.driveId = rattach.getDriveId();
			this.fileId = rattach.getRattachId();
			this.title = rattach.getDescription();
			this.date = rattach.getDate();
			this.icon = rattach.getIcon();
			this.confidential = rattach.getConfidential();
			this.domain = rattach.getDomainName();
			this.domainId = rattach.getDomainId();
			this.domainDescription = rattach.getDomainDescription();
			this.md5 = rattach.getMd5();
		}
		
		public String getAonType(){
			return aonType;
		}
		
		public void setAonType(String aonType){
			this.aonType=aonType;
		}
		
		public short getType(){
			return type;
		}
		
		public void setType(short type){
			this.type=type;
		}
		
		public Integer getCategory(){
			return category;
		}
		
		public void setCategory(Integer category){
			this.category=category;
		}
		
		public String getTitle(){
			return title;
		}
		
		public void setTitle(String title){
			this.title=title;
		}
		
		public Byte getMimetype(){
			return mimetype;
		}
		
		public void setMimetype(Byte mimetype){
			this.mimetype=mimetype;
		}
		
		public String getDriveId(){
			return driveId;
		}
		
		public void setDriveId(String driveId){
			this.driveId=driveId;
		}
		
		public int getFileId(){
			return fileId;
		}
		
		public void setFileId(int fileId){
			this.fileId=fileId;
		}
		
		public Vector<String> getEmails(){
			return emails;
		}
		
		public void setEmails(Vector<String> emails){
			this.emails=emails;
		}

		public Boolean getIsNomina(){
			return isNomina;
		}
		
		public void setIsNomina(Boolean isNomina){
			this.isNomina=isNomina;
		}

		public Date getDate() {
			return date;
		}

		public void setDate(Date date) {
			this.date = date;
		}
		
		public Integer getSize() {
			return size;
		}

		public void setSize(Integer size) {
			this.size = size;
		}

		public String getSizeStr() {
			return sizeStr;
		}

		public void setSizeStr(String sizeStr) {
			this.sizeStr = sizeStr;
		}

		public String getCategoryStr() {
			return categoryStr;
		}

		public void setCategoryStr(String categoryStr) {
			this.categoryStr = categoryStr;
		}	
		
		

		public String getTagsStr() {
			return tagsStr;
		}

		public void setTagsStr(String tagsStr) {
			this.tagsStr = tagsStr;
		}
		
		public String getDateStr(){
			
			return dateStr;
		}
		
		public void setDateStr(String dateStr){
		
			this.dateStr=dateStr;
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


		public String getDomain() {
			return domain;
		}

		public void setDomain(String domain) {
			this.domain = domain;
		}
		public Integer getDomainId() {
			return domainId;
		}

		public void setDomainId(Integer domainId) {
			this.domainId = domainId;
		}

		public byte[] getData() {
			return data;
		}

		public void setData(byte[] data) {
			this.data = data;
		}

		public Byte getConf() {
			return conf;
		}

		public void setConf(Byte conf) {
			this.conf = conf;
		}

		public java.sql.Date getDateSql() {
			return dateSql;
		}

		public void setDateSql(java.sql.Date dateSql) {
			this.dateSql = dateSql;
		}

		public String getDomainDescription() {
			return domainDescription;
		}

		public void setDomainDescription(String domainDescription) {
			this.domainDescription = domainDescription;
		}

		public Boolean getIsParent() {
			return isParent;
		}

		public void setIsParent(Boolean isParent) {
			this.isParent = isParent;
		}

		public Boolean getIsDrive() {
			return isDrive;
		}

		public void setIsDrive(Boolean isDrive) {
			this.isDrive = isDrive;
		}

		public Boolean getIsGdocs() {
			return isGdocs;
		}

		public void setIsGdocs(Boolean isGdocs) {
			this.isGdocs = isGdocs;
		}

		public String getMimeString() {
			return mimeString;
		}

		public void setMimeString(String mimeString) {
			this.mimeString = mimeString;
		}

		public String getMd5() {
			return md5;
		}

		public void setMd5(String md5) {
			this.md5 = md5;
		}

		


		

	}

