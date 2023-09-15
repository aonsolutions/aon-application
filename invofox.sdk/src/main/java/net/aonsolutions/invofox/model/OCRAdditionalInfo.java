package net.aonsolutions.invofox.model;

import java.io.Serializable;
import java.util.Optional;

public class OCRAdditionalInfo implements Serializable {
	
	private static final long serialVersionUID = -6744366796412443072L;
	
	private String description;
	private String path;
	private String message;

	public Optional<String> getDescription() {
		return Optional.ofNullable(description);
	}
	public OCRAdditionalInfo setDescription(String description) {
		this.description = description;
		return this;
	}
	
	public Optional<String> getPath() {
		return Optional.ofNullable(path);
	}
	public OCRAdditionalInfo setPath(String path) {
		this.path = path;
		return this;
	}
	
	public Optional<String> getMessage() {
		return Optional.ofNullable(message);
	}
	public OCRAdditionalInfo setMessage(String message) {
		this.message = message;
		return this;
	}
	
	
}
