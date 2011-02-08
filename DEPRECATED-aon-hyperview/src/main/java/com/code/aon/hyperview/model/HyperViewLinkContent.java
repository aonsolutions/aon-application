package com.code.aon.hyperview.model;

public class HyperViewLinkContent implements IHyperViewContent {

	private String linkLabel;
	private String linkUrl;
	private String linkUrlPattern;

	public String getLabel() {
		return null;
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
		if (this.linkUrlPattern == null) {
			this.linkUrlPattern = linkUrl;
		}
	}

	public String getLinkUrlPattern() {
		return linkUrlPattern;
	}

	public boolean isFormView() {
		return false;
	}

	public boolean isListView() {
		return false;
	}

	public boolean isImageView() {
		return false;
	}

	public boolean isContentCompositeView() {
		return false;
	}

	public boolean isHyperLinkView() {
		return true;
	}
}
