package com.code.aon.faces.component;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;

public class ComponentInfo {

	private String localName;
	
	private List<AttributeInfo> attributes;
	
	public ComponentInfo() {
		this.attributes = new ArrayList<AttributeInfo>();
	}

	public String getLocalName() {
		return localName;
	}

	public void setLocalName(String localName) {
		this.localName = localName;
	}

	public AttributeInfo getAttributeInfo( String name ) {
		for( AttributeInfo ai : attributes ) {
			if ( ai.getName().equals(name) ) {
				return ai;
			}
		}
		return null;
	}

	public List<AttributeInfo> getAttributes() {
		return attributes;
	}

	public void addAttribute(AttributeInfo attribute) {
		this.attributes.add( attribute );
	}
	
	@Override
	public String toString() {
	     return new ToStringBuilder(this).append("localName", localName).toString();
	}
	
}
