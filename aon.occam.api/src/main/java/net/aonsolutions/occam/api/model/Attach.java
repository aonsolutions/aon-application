package net.aonsolutions.occam.api.model;

import java.io.Serializable;
import java.util.Date;

import net.aonsolutions.occam.api.model.type.AttachType;
import net.aonsolutions.occam.api.model.type.MimeType;

public class Attach implements Serializable {

    private static final long serialVersionUID = -4547029590967697857L;
    
	public static final long ONE_KB = 1024;
    public static final long ONE_MB = ONE_KB * ONE_KB;
    public static final long ONE_GB = ONE_KB * ONE_MB;
	
    private AttachType attachType;
	private Integer attachModule;
	private String attachURL;
	
	//---------- Generic
	private Integer id;
	private Integer domain;
	private MimeType mimeType;
	private String description;
	private byte[] data;
	private Date date;
	private Byte type;
	private String driveId;
	private Integer scope;
	private boolean confidential;

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
	private boolean isDrive;
	
	
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

	public boolean isConfidential() {
		return confidential;
	}
	public Attach setConfidential(boolean confidential) {
		this.confidential = confidential;
		return this;
	}

	public Integer getDomain() {
		return domain;
	}
	public Attach setDomain(Integer domain) {
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
	
	public Byte getSourceType() {
		return sourceType;
	}
	public Attach setSourceType(Byte sourceType) {
		this.sourceType = sourceType;
		return this;
	}
	
	public boolean getIsDrive() {
		return isDrive;
	}
	public Attach setIsDrive(boolean isDrive) {
		this.isDrive = isDrive;
		return this;
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
