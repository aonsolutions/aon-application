package net.aonsolutions.infovox.model;

public class OCRAdditionalInfo {
	
	private String description;
	private String path;
	private String message;

	public String getDescription() {
		return description;
	}
	public OCRAdditionalInfo setDescription(String description) {
		this.description = description;
		return this;
	}
	
	public String getPath() {
		return path;
	}
	public OCRAdditionalInfo setPath(String path) {
		this.path = path;
		return this;
	}
	
	public String getMessage() {
		return message;
	}
	public OCRAdditionalInfo setMessage(String message) {
		this.message = message;
		return this;
	}
	
	
}
