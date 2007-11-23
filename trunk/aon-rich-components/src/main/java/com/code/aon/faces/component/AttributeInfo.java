package com.code.aon.faces.component;

import javax.faces.component.UIComponent;

import org.apache.commons.lang.StringUtils;

import com.sun.facelets.FaceletContext;

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

	public void update( AonComponentHandler aonComponent, FaceletContext ctx, UIComponent component ) {
		if ( ! isIgnore() ) {
			if ( isForce() || (!aonComponent.hasValue(ctx, getName())) ) {
				String finalName = StringUtils.defaultString(getAlias(), getName());	
				getAttributeType().setValue(ctx.getFacesContext(), component, finalName, getValue());
			}
		}
	}
	
}
