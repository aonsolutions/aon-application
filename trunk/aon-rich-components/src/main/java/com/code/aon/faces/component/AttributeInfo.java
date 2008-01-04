package com.code.aon.faces.component;

import javax.faces.component.UIComponent;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.code.aon.faces.component.util.FaceletUtil;
import com.sun.facelets.FaceletContext;
import com.sun.facelets.tag.Tag;

public class AttributeInfo {

	private String name;
	
	private String value;
	
	private boolean force;
	
	private boolean ignore;
	
	private String alias;
	
	private AttributeType type;
	
	public AttributeInfo() {
		type = AttributeType.STRING;
	}

	public AttributeInfo( String name, String value ) {
		this();
		setName(name);
		setValue(value);
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public boolean isForce() {
		return force;
	}

	public void setForce(boolean force) {
		this.force = force;
	}

	public boolean isIgnore() {
		return ignore;
	}

	public void setIgnore(boolean ignore) {
		this.ignore = ignore;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public AttributeType getAttributeType() {
		return type;
	}

	public void setType(int type) {
		this.type = AttributeType.values()[type];
	}

	public void update( Tag tag, FaceletContext ctx, UIComponent component ) {
		if ( (! isIgnore()) && (getValue() != null) ) {
			if ( isForce() || (!FaceletUtil.hasValue(ctx, tag, getName())) ) {
				getAttributeType().setValue(ctx.getFacesContext(), component, getName(), getValue());
			}
		}
	}
	
	@Override
	public String toString() {
	     return ToStringBuilder.reflectionToString(this);
	}
	
}
