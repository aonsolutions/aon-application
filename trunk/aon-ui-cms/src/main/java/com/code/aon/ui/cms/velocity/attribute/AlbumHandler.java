package com.code.aon.ui.cms.velocity.attribute;

import java.util.Date;

import com.code.aon.cms.AlbumDetail;

public class AlbumHandler {

	private String title;
	
	private String description;
	
	private String image;
	
	private Date date;
	
	private String alt;

	public AlbumHandler (AlbumDetail detail) {
		title = detail.getTitle();
		description = detail.getDescription();
		image = detail.getAlbum().getImage();
		date = detail.getAlbum().getPublishDate();
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

	public Date getDate() {
		return date;
	}

	public String getAlt() {
		return alt;
	}

	
}
