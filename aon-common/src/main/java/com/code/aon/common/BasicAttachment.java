package com.code.aon.common;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.ArrayUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.enumeration.MimeType;

public class BasicAttachment implements IAttachment {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private Integer id;
	
	private Integer domain;

	private MimeType mimeType;
	
	private byte[] data;
	
	private String description;
	
	private String aonType;
	
	private String driveId;
	
	@Override
	public int getDomain() {
		return domain;
	}
	
	@Override
	public void setDomain(int domain) {
		this.domain = domain;
	}

	@Override
	public Integer getId() {
		return id;
	}

	@Override
	public void setId(Integer primaryKey) {
		this.id = primaryKey;
	}
	
	@Override
       	public String getDriveId(){
		return driveId;
	}
	
	@Override
       	public void setDriveId(String driveId) {
		this.driveId = driveId;
	}

	@Override
	public MimeType getMimeType() {
		return mimeType;
	}

	@Override
	public void setMimeType(MimeType mimeType) {
		this.mimeType = mimeType;
	}

	@Override
	public byte[] getData() {
		return data;
	}

	@Override
	public void setData(byte[] data) {
		this.data = data;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public Integer getSize() {
		return ArrayUtils.getLength(this.data);
	}

	@Override
	public void setSize(Integer size) {
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public String getMD5() {
		if (! ArrayUtils.isEmpty(this.data) ) {
			return DigestUtils.md5Hex(this.data);	
		}
		return null;
	}

	@Override
	public String getAonType() {
		return aonType;
	}

	@Override
	public void setAonType(String aonType) {
		this.aonType = aonType;
	}
	
}
