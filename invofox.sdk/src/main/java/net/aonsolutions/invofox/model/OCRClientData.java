package net.aonsolutions.invofox.model;

import java.io.Serializable;
import java.util.Optional;

public class OCRClientData implements Serializable {
	
	private static final long serialVersionUID = -426369126979614494L;
	
	private String filename;
	private String key;
	private String value;
	
	private OCRS3Object s3Object;
	
	private Integer rawdoc;
	
	public Optional<String> getFilename() {
		return Optional.ofNullable(filename);
	}
	public OCRClientData setFilename(String filename) {
		this.filename = filename;
		return this;
	}
	
	public Optional<String> getKey() {
		return Optional.ofNullable(key);
	}
	public OCRClientData setKey(String key) {
		this.key = key;
		return this;
	}
	
	public Optional<String> getValue() {
		return Optional.ofNullable(value);
	}
	public OCRClientData setValue(String value) {
		this.value = value;
		return this;
	}
	
	public OCRClientData  setS3Object(OCRS3Object s3Object) {
	    this.s3Object = s3Object;
	    return this;
	}
	
	public Optional<OCRS3Object> getS3Object() {
	    return Optional.ofNullable(s3Object);
	}
	
	public Integer getRawdoc() {
		return rawdoc;
	}
	
	public OCRClientData setRawdoc(Integer rawdoc) {
		this.rawdoc = rawdoc;
		return this;
	}
	
}
