package com.code.aon.ui.cms.velocity.attribute;

import com.code.aon.cms.BannerDetail;

public class BannerHandler {

	private String description;
	
	private String image;
	
	private String label;
	
	private String url;

	public BannerHandler (BannerDetail d) {
		this.description = d.getDescription();
		this.image = d.getImage();
		this.label = d.getLabel();
		this.url = d.getUrl();
	}

	public String getDescription() {
		return description;
	}

	public String getImage() {
		return image;
	}

	public String getLabel() {
		return label;
	}

	public String getUrl() {
		return url;
	}

	
}
