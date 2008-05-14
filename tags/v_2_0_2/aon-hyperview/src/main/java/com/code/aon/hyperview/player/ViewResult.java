/**
 * 
 */
package com.code.aon.hyperview.player;

public class ViewResult {
	Object[] values;

	String linkLabel;

	String linkUrl;
	
	public Object[] getValues() {
		return values;
	}

	public void setValues(Object[] values) {
		this.values = values;
	}

	public String getLinkLabel() {
		return linkLabel;
	}

	public void setLinkLabel(String linkLabel) {
		this.linkLabel = linkLabel;
	}

	public String getLinkUrl() {
		return linkUrl;
	}

	public void setLinkUrl(String linkUrl) {
		this.linkUrl = linkUrl;
	}
	
	public boolean isLinkable() {
		return (linkUrl != null);
	}

}