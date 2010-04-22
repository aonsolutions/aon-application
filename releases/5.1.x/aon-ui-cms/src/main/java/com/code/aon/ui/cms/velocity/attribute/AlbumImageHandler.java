package com.code.aon.ui.cms.velocity.attribute;

import com.code.aon.cms.AlbumImageDetail;
import com.code.aon.cms.enumeration.Templates;

public class AlbumImageHandler {

	private String id;
	
	private String title;
	
	private String description;
	
	private String image;
	
	private String thumbnail;
	
	private String alt;
	
	private String url;

	public AlbumImageHandler (AlbumImageDetail detail) {
		this.id = ""+detail.getAlbumImage().getId();
		this.title = detail.getTitle();
		this.description = detail.getDescription();
		this.image = detail.getAlbumImage().getImage();
		this.thumbnail = detail.getAlbumImage().getThumbnail();
		this.alt = detail.getAlt();
		this.url = Templates.ALBUM_IMAGES.getHtmlName();
		this.url = this.url.replaceAll("%NAME%", "ALBUM_IMAGE_"+this.id);
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

	public String getUrl() {
		return url;
	}

	public String getId() {
		return id;
	}

	
}
