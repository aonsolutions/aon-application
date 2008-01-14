package com.code.aon.ui.cms.velocity.attribute;

import com.code.aon.cms.AlbumImageDetail;

public class AlbumImageHandler {

	private String title;
	
	private String description;
	
	private String image;
	
	private String thumbnail;
	
	private String alt;

	public AlbumImageHandler (AlbumImageDetail detail) {
		title = detail.getTitle();
		description = detail.getDescription();
		image = detail.getAlbumImage().getImage();
		thumbnail = detail.getAlbumImage().getThumbnail();
		alt = detail.getAlt();
	}
	
	public String getDescription() {
		return description;
	}

	public String getImage() {
		return image;
	}

	public String getTitle() {
		return title;
	}

	public String getThumbnail() {
		return thumbnail;
	}

	public String getAlt() {
		return alt;
	}

	
}
