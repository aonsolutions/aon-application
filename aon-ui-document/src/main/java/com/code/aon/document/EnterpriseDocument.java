package com.code.aon.document;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.alfresco.webservice.types.Reference;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.document.dao.AlfrescoDAO;

public class EnterpriseDocument implements IAlfrescoDocument {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(EnterpriseDocument.class);
	
	private static final long serialVersionUID = 6588665319488331110L;

	private Reference id;
	
	private String name;
	
	private String description;
	
	private Date created; 
	
	private Integer enterpriseId;
	
	private MimeType mimeType;
	
	private byte[] data;
	
	private AlfrescoCategory[] categories;
	
	private AlfrescoDAO dao;
	
	public void setDao(AlfrescoDAO dao) {
		this.dao = dao;
	}

	public Reference getId() {
		return id;
	}

	public void setId(Reference id) {
		this.id = id;
	}

	public MimeType getMimeType() {
		return mimeType;
	}

	public void setMimeType(MimeType mimeType) {
		this.mimeType = mimeType;
	}

	public byte[] getData() {
		if ( data == null ) {
			try {
				data = dao.getContent(getId());
			} catch (DAOException e) {
				LOGGER.error( "Error reading content of " + getId(), e );
			}
		}
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
	
	public AlfrescoCategory[] getCategories() {
		return categories;
	}

	public void setCategories(AlfrescoCategory[] categories) {
		this.categories = categories;
	}

	public String getCategoryList() {
		if (! ArrayUtils.isEmpty(categories) ) {
			List<String> list = new LinkedList<String>();
			for( AlfrescoCategory category : categories ) {
				list.add(category.getName());
			}
			return StringUtils.join(list, ", ");			
		}
		return null;
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
			.toHashCode();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).
			append("created", created).
			append("description", description).
			append("enterpriseId", enterpriseId).
			append("id", id).
			append("mimeType", mimeType).
			append("name", name).
			toString();
	}
	
}
