package com.esferalia.aon.occam.api.model.doc;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import com.esferalia.aon.occam.api.GwtIncompatible;

public class ExternalDoc<T extends Enum<?>> extends Doc<T> {

	private static final long serialVersionUID = -4316348857841270394L;
	
	private String aonTable;
	private ExternalStorage externalStorage;
	private String s3Bucket;
	private String s3Key;
	private String driveId;
	private Integer aonId;
	private String url; // AON SHORT URL
	
	public String getAonTable() {
		return aonTable;
	}
	
	public ExternalDoc<T> setAonTable(String aonTable) {
		this.aonTable = aonTable;
		return this;
	}
	
	public ExternalStorage getExternalStorage() {
		return externalStorage;
	}
	
	public ExternalDoc<T> setExternalStorage(ExternalStorage externalStorage) {
		this.externalStorage = externalStorage;
		return this;
	}
	
	public Integer getAonId() {
		return aonId;
	}
	
	public ExternalDoc<T> setAonId(Integer aonId) {
		this.aonId = aonId;
		return this;
	}
	
	public String getDriveId() {
		return driveId;
	}
	
	public ExternalDoc<T> setDriveId(String driveId) {
		this.driveId = driveId;
		return this;
	}
	
	public String getS3Bucket() {
		return s3Bucket;
	}
	
	public ExternalDoc<T> setS3Bucket(String s3Bucket) {
		this.s3Bucket = s3Bucket;
		return this;
	}
	
	public String getS3Key() {
		return this.s3Key;
	}
	
	public ExternalDoc<T> setS3Key(String s3Key) {
		this.s3Key = s3Key;
		return this;
	}

	public String getUrl() {
		return url;
	}
	
	public ExternalDoc<T> setUrl(String url) {
		this.url = url;
		return this;
	}
	
	@Override
	@GwtIncompatible
	public URL getDownloadURL() {
		return getDownloadURL(null);
	}
	
	@Override
	@GwtIncompatible
	public URL getDownloadURL(String contentDisposition) {
		try {
			return new URI(getUrl()).toURL();
		} catch (URISyntaxException | MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
