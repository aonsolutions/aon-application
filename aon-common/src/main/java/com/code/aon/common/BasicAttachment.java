package com.code.aon.common;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.ArrayUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.enumeration.MimeType;

public class BasicAttachment implements IAttachment {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private Integer id;
	
	private MimeType mimeType;
	
	private byte[] data;
	
	private String description;

	@Override
	public Integer getId() {
		return id;
	}

	@Override
	public void setId(Integer primaryKey) {
		this.id = primaryKey;
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
	
}