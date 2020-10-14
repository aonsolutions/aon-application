package com.esferalia.aon.occam.api.model.attachment;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.registry.Category;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.type.MimeType;

@SuppressWarnings("serial")
public class Attach implements Serializable {

    public static final long ONE_KB = 1024;
    public static final long ONE_MB = ONE_KB * ONE_KB;
    public static final long ONE_GB = ONE_KB * ONE_MB;
	
	AttachType attachType;
	private Integer attachModule;
	private String attachURL;
	
	//---------- Generic
	private Integer id;
	private Domain domain;
	private MimeType mimeType;
	private String description;
	private byte[] data;
	private Date date;
	private Byte type;
	private String driveId;
	private Integer scope;
	private Boolean confidential;

	//---------- Registry
	private Integer category;
	private String dparentId;
	
	//---------- Sepe & Payroll
	private Integer sourceBatch;
	private Byte sourceType;
	
	//---------- Audit
	private String creationUser;
	private Date creationDate;
	private String modificationUser;
	private Date modificationDate;
	
	//---------- Auxiliar
	private String icon;
	private String md5;
	private Boolean isDrive;
	
	//---------- DOCUMENTAL

	private Scope fullScope;
	private Category fullCategory;
	private LinkedList<Tag> tagList;
	
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

	public Byte getType() {
		return type;
	}

	public Attach setType(Byte type) {
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
	public String getAttachURL() {
		return attachURL;
	}
	public Attach setAttachURL(String attachURL) {
		this.attachURL = attachURL;
		return this;
	}
	public Integer getSourceBatch() {
		return sourceBatch;
	}
	public Attach setSourceBatch(Integer sourceBatch) {
		this.sourceBatch = sourceBatch;
		return this;
	}
	
	public Integer getSourceId() {
		return sourceBatch;
	}
	
	public Attach setSourceId(Integer sourceBatch) {
		this.sourceBatch = sourceBatch;
		return this;
	}
	
	public Byte getSourceType() {
		return sourceType;
	}
	public Attach setSourceType(Byte sourceType) {
		this.sourceType = sourceType;
		return this;
	}
	
	public Byte getSource() {
		return sourceType;
	}
	
	public Attach setSource(Byte sourceType) {
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
	
	
	
	
	public Scope getFullScope() {
		return fullScope;
	}
	public Attach setFullScope(Scope fullScope) {
		this.fullScope = fullScope;
		return this;
	}
	public Category getFullCategory() {
		return fullCategory;
	}
	public Attach setFullCategory(Category fullCategory) {
		this.fullCategory = fullCategory;
		return this;
	}
	public LinkedList<Tag> getTagList() {
		return tagList;
	}
	public Attach setTagList(LinkedList<Tag> tagList) {
		this.tagList = tagList;
		return this;
	}
	
	public String getTagListString(){
		String str = "";
		for(Tag t : getTagList()){
			if(!str.equals("")) str = str+", ";
			str = str + t.getName();
		}
		return str;
	}
	
	public Integer getSize(){
		return Integer.parseInt(getDparentId() != null ? getDparentId() : "0");
	}
	
	public String getSizeString(){
		return byteCountToDisplaySize(getSize());
	}
	
	public static String byteCountToDisplaySize(long size) {
	        String displaySize;

	        if (size / ONE_GB > 0) {
	            displaySize = String.valueOf(size / ONE_GB) + " GB";
	        } else if (size / ONE_MB > 0) {
	            displaySize = String.valueOf(size / ONE_MB) + " MB";
	        } else if (size / ONE_KB > 0) {
	            displaySize = String.valueOf(size / ONE_KB) + " KB";
	        } else {
	            displaySize = String.valueOf(size) + " bytes";
	        }
	        return displaySize;
	}
}
