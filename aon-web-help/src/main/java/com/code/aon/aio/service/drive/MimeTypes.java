package com.code.aon.aio.service.drive;

public enum MimeTypes {

	HTML("text/html"),
	PDF("application/pdf"),
	MP4("video/mp4"),
	FOLDER("application/vnd.google-apps.folder");
	
	private String mime;
	
	private MimeTypes(String mime) {
		this.mime = mime;
	}
	
	public String getMime() {
		return this.mime;
	}
	
	/**
	 * Get enum entry by mime type
	 * @param mime
	 * @return
	 */
	public static MimeTypes valueOfMime(String mime) {
	    for (MimeTypes e : values()) {
	        if (e.mime.equals(mime)) {
	            return e;
	        }
	    }
	    return null;
	}
}
