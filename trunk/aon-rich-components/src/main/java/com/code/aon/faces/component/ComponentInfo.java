package com.code.aon.faces.component;

import java.util.ArrayList;
import java.util.List;

public class ComponentInfo {

	private String localName;
	
	private String namespace;
	
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

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public String getFullName() {
		return namespace + "/" + localName;
	}

	public List<AttributeInfo> getAttributes() {
		return attributes;
	}

	public void addAttribute(AttributeInfo attribute) {
		this.attributes.add( attribute );
	}
	
}
