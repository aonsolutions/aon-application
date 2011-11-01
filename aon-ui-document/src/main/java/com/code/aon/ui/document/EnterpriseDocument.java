package com.code.aon.ui.document;

import java.util.Date;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.code.aon.common.enumeration.MimeType;

public class EnterpriseDocument implements IAlfrescoTransferObject {
	
	private static final long serialVersionUID = 6588665319488331110L;

	private String id;
	
	private String path;
	
	private String name;
	
	private String description;
	
	private Date created; 
	
	private Integer enterpriseId;
	
	private MimeType mimeType;
	
	private byte[] data;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public MimeType getMimeType() {
		return mimeType;
	}

	public void setMimeType(MimeType mimeType) {
		this.mimeType = mimeType;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Integer getEnterpriseId() {
		return enterpriseId;
	}

	public void setEnterpriseId(Integer enterpriseId) {
		this.enterpriseId = enterpriseId;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final EnterpriseDocument o = (EnterpriseDocument) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.created, o.created)
				.append(this.description, o.description)
				.append(this.enterpriseId, o.enterpriseId)
				.append(this.name, o.name)
				.append(this.path, o.path)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(created)
			.append(data)
			.append(description)
			.append(enterpriseId)
			.append(id)
			.append(mimeType)
			.append(name)
			.append(path)
			.toHashCode();
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
}
