package com.code.aon.document;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.Transient;

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
import com.code.aon.company.Enterprise;
import com.code.aon.document.dao.AlfrescoDAO;
import com.code.aon.project.Project;

public class EnterpriseDocument implements IAlfrescoDocument {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(EnterpriseDocument.class);
	
	private static final long serialVersionUID = 6588665319488331110L;

	private Reference id;
	
	private String name;
	
	private String description;
	
	private String title;
	
	private Date createdDate; 
	
	private Date modifiedDate;
	
	private Date referenceDate;
	
	private Enterprise enterprise;

	private Project project;
	
	private MimeType mimeType;
	
	private byte[] data;
	
	private AlfrescoCategory[] categories;
	
	private AlfrescoDAO dao;
	
	private AlfrescoGroup scope;
	
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
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public Date getReferenceDate() {
		return referenceDate;
	}

	public void setReferenceDate(Date referenceDate) {
		this.referenceDate = referenceDate;
	}

	public Enterprise getEnterprise() {
		return enterprise;
	}

	public void setEnterprise(Enterprise enterprise) {
		this.enterprise = enterprise;
	}
	
	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
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
	
	@Transient
	public AlfrescoGroup getScope() {
		return scope;
	}

	public void setScope(AlfrescoGroup scope) {
		this.scope = scope;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final EnterpriseDocument o = (EnterpriseDocument) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.createdDate, o.createdDate)
				.append(this.description, o.description)
				.append(this.enterprise, o.enterprise)
				.append(this.name, o.name)
				.append(this.mimeType, o.mimeType)
				.append(this.modifiedDate, o.modifiedDate)
				.append(this.project, o.project)
				.append(this.referenceDate, o.referenceDate)
				.append(this.scope, o.scope)
				.append(this.title, o.title)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(createdDate)
			.append(data)
			.append(description)
			.append(enterprise)
			.append(id)
			.append(name)
			.append(mimeType)
			.append(modifiedDate)
			.append(referenceDate)
			.append(scope)
			.append(title)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).
			append("createdDate", createdDate).
			append("description", description).
			append("enterprise", (enterprise != null) ? enterprise.getId() : null).
			append("id", id).
			append("name", name).
			append("mimeType", mimeType).
			append("modifiedDate", modifiedDate).
			append("project", (project != null) ? project.getId() : null).
			append("referenceDate", referenceDate).
			append("scope", (scope != null) ? scope.getName() : null).
			append("title", title).
			toString();
	}
	
}