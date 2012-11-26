package com.code.aon.document;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.code.aon.common.ITransferObject;

public class AlfrescoGroup implements ITransferObject {
	
	private static final long serialVersionUID = 2846011950376303032L;

	private String name;
	
	public AlfrescoGroup() {
	}
	
	public AlfrescoGroup(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final AlfrescoGroup o = (AlfrescoGroup) obj;
		return ObjectUtils.equals(getName(), o.getName());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(name)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).
			append("name", name).
			toString();
	}

}
