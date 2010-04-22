package com.code.aon.hyperview.model;

public class HyperViewImageContent implements IHyperViewContent {

	private String imageLabel;
	private String imageUrl;
	private String imageUrlPattern;
	
	public String getLabel() {
		return null;
	}

	public String getImageLabel() {
		return imageLabel;
	}

	public void setImageLabel(String imageLabel) {
		this.imageLabel = imageLabel;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
		if (this.imageUrlPattern == null) {
			this.imageUrlPattern = imageUrl;
		}
	}

	public String getImageUrlPattern() {
		return imageUrlPattern;
	}


	public boolean isFormView() {
		return false;
	}

	public boolean isListView() {
		return false;
	}

	public boolean isContentCompositeView() {
		return false;
	}

	public boolean isHyperLinkView() {
		return false;
	}
	
	public boolean isImageView() {
		return true;
	}
}
