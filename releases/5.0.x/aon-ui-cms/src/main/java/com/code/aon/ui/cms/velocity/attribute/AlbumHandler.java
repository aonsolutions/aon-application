package com.code.aon.ui.cms.velocity.attribute;

import java.util.Date;

import com.code.aon.cms.AlbumDetail;
import com.code.aon.cms.enumeration.Templates;

public class AlbumHandler {

	private String alias;
	
	private String title;
	
	private String description;
	
	private String image;
	
	private Date date;
	
	private String alt;
	
	private String url;

	public AlbumHandler (AlbumDetail detail) {
		this.alias = detail.getAlbum().getAlias();
		this.title = detail.getTitle();
		this.description = detail.getDescription();
		this.image = detail.getAlbum().getImage();
		this.date = detail.getAlbum().getPublishDate();
		this.alt = detail.getAlt();
		this.url = Templates.ALBUM_IMAGES.getHtmlName();
		this.url = this.url.replaceAll("%NAME%", this.alias);
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

	public String getUrl() {
		return url;
	}

	public String getAlias() {
		return alias;
	}
	
	
}
