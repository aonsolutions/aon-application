package com.code.aon.document;

import static com.code.aon.document.IAlfrescoConstants.CATEGORY_ROOT_SHORT;
import static com.code.aon.document.IAlfrescoConstants.CONTENT_MODEL;
import static com.code.aon.document.IAlfrescoConstants.CONTENT_PREFFIX;

import java.util.LinkedList;
import java.util.List;

import org.alfresco.webservice.types.Reference;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

public class AlfrescoCategory implements IAlfrescoTransferObject {

	private static final long serialVersionUID = -8322661069263146878L;

	public static final String AON_CLASIFICATION = "cm:generalclassifiable/cm:AON";
	
	private Reference id;
	
	private String name;
	
	private String description;
	
	private List<AlfrescoCategory> categories;
	
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
	
	public List<AlfrescoCategory> getCategories() {
		return categories;
	}

	public void setCategories(List<AlfrescoCategory> categories) {
		this.categories = categories;
	}

	public String getCategoriesString() {
		if ( (categories != null) && (!categories.isEmpty()) ) {
			List<String> names = new LinkedList<String>();
			for( AlfrescoCategory category : categories ) {
				names.add( category.getName() );
			}
			return StringUtils.join(names, ", ");
		}
		return null;
	}
	
	public String getSearchValue() {
		StringBuffer sb = new StringBuffer();
		sb.append( "\"" );
		sb.append( getSearhPath(id) );
		sb.append( "/member\"" );
		return sb.toString();
	}

	public String getSearhPath() {
		return getSearhPath(getId());
	}
	
	public static String getSearhPath( Reference reference ) {
		String path = StringUtils.replace(reference.getPath(), CONTENT_MODEL, CONTENT_PREFFIX);
		return StringUtils.removeStart(path, "/" + CATEGORY_ROOT_SHORT + "/" );
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
		return new ToStringBuilder(this).
			append("description", description).
			append("id", (id != null) ? BasicAlfresco.getId(id) : null ).
			append("name", name).
			toString();
	}

}