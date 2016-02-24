package com.esferalia.aon.gwt.office.client;

import com.esferalia.aon.gwt.office.client.models.issues.JsLabel;
import com.google.gwt.http.client.URL;

public class DefaultAonTagIssueSelected implements LabelSelected {
	
	protected JsLabel label;
	private String name;
	
	public DefaultAonTagIssueSelected(JsLabel label) {
		this.label = label;
		this.name = URL.decode(label.getName());
	}
	
	@Override
	public Integer getId() {		
		return label.getId();
	}
	
	@Override
	public Integer getDomain() {		
		return label.getDomain();
	}
	
	@Override
	public byte getType() {	
		return label.getType();
	}
	
	@Override
	public String getName() {	
		return this.name;
	}
	
	@Override
	public String getColor() {		
		return label.getColor();
	}
}
