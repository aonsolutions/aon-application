package com.esferalia.aon.occam.api.model.doc;

import java.net.URL;

public interface IDoc<T extends Enum<?>>  {

	public T getType();
	
	public URL getDownloadURL();

	public String getDescription();

	public URL getDownloadURL(String contentDisposition);
	
	public <M extends Enum<?>> M getMimeType(Class<M> enumClass);
	
}
