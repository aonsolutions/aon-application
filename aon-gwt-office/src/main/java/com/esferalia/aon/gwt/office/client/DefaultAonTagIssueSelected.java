package com.esferalia.aon.gwt.office.client;

import com.esferalia.aon.gwt.office.client.models.issues.JsLabel;

public class DefaultAonTagIssueSelected implements LabelSelected {
	
	protected JsLabel label;
	
	public DefaultAonTagIssueSelected(JsLabel label) {
		this.label = label;
	}
	
	@Override
	public Integer getId() {		
		return label.getId();
	}
	
	@Override
	public byte getType() {	
		return label.getType();
	}
	
	@Override
	public String getName() {	
		return label.getName();
	}
	
	@Override
	public String getColor() {		
		return label.getColor();
	}
}
