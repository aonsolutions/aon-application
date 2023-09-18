package net.aonsolutions.invofox.model;

import java.io.Serializable;
import java.util.Optional;

public class OCRClientData implements Serializable {
	
	private static final long serialVersionUID = -426369126979614494L;
	
	private String filename;
	private String key;
	private String value;
	
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
}
