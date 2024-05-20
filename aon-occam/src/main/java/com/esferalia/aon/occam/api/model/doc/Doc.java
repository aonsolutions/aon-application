package com.esferalia.aon.occam.api.model.doc;

import java.net.URL;
import java.util.Date;

import com.esferalia.aon.occam.api.model.type.MimeType;

public abstract class Doc<T extends Enum<?>>  {
	
	private T type;
	private Integer id;
	private Integer domain;
	private MimeType mimeType;
	private String description;
	private Date date;
	
	public T getType() {
		return type;
	}
	
	public Doc<T> setType(T type) {
		this.type = type;
		return this;
	}
	
	public Integer getId() {
		return id;
	}
	
	public Doc<T> setId(Integer id) {
		this.id = id;
		return this;
	}
	
	public Integer getDomain() {
		return domain;
	}
	
	public Doc<T> setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}

	public String getDescription() {
		return description;
	}
	
	public Doc<T> setDescription(String description) {
		this.description = description;
		return this;
	}
	
	public MimeType getMimeType() {
		return mimeType;
	}
	
	public Doc<T> setMimeType(MimeType mimeType) {
		this.mimeType = mimeType;
		return this;
	}
	
	public Doc<T> setMimeType(Byte mimeType) {
		this.mimeType = MimeType.safeValueOf(mimeType);
		return this;
	}
	
	public Date getDate() {
		return date;
	}
	
	public Doc<T> setDate(Date date) {
		this.date = date;
		return this;
	}
	
	public abstract URL getDownloadURL();

	public abstract  URL getDownloadURL(String contentDisposition);
	
}
