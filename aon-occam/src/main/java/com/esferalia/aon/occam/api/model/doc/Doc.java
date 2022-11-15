package com.esferalia.aon.occam.api.model.doc;

import java.net.URL;

import com.esferalia.aon.occam.api.model.type.MimeType;

public abstract class Doc<T extends Enum<?>>  {
	
	private T type;
	private MimeType mimeType;
	private String description;
	
	public T getType() {
		return type;
	}
	
	public Doc<T> setType(T type) {
		this.type = type;
		return this;
	}

	public Doc<T> setDescription(String description) {
		this.description = description;
		return this;
	}
	
	public String getDescription() {
		return description;
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
	
	
	public abstract URL getDownloadURL();

	public abstract  URL getDownloadURL(String contentDisposition);
	
}
