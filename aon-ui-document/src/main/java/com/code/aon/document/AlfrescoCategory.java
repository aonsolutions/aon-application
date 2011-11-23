package com.code.aon.document;

import org.alfresco.webservice.types.Reference;
import org.alfresco.webservice.util.ISO9075;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;

public class AlfrescoCategory implements IAlfrescoTransferObject {

	private static final long serialVersionUID = -8322661069263146878L;

	public static final String AON_CLASIFICATION = "cm:generalclassifiable/cm:AON";
	
	private Reference id;
	
	private String name;
	
	private String description;
	
	public Reference getId() {
		return id;
	}

	public void setId(Reference id) {
		this.id = id;
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
	
	public String getSearchValue() {
		StringBuffer sb = new StringBuffer();
		sb.append( "\"" );
		sb.append( AON_CLASIFICATION );
		sb.append( "/cm:" );
		sb.append( ISO9075.encode(getName()) );
		sb.append( "/member\"" );
		return sb.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final AlfrescoCategory o = (AlfrescoCategory) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.description, o.description)
				.append(this.name, o.name)
				.isEquals();
		}
		return BasicAlfresco.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(description)
			.append(id)
			.append(name)
			.toHashCode();
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);	
	}

}