package es.translogia.tedi;

import org.json.JSONObject;

public class TediFile {
	public TediFile() {}
	
	public TediFile(JSONObject json) {
		if(json != null) {
			this.url = json.optString("url");
			this.thumbUrl = json.optString("thumb_url");
			this.contentType = json.optString("content_type");
		}
	}
	
	private String url;
	private String thumbUrl;
	private String contentType;
	
	public String getUrl() {
		return url;
	}

	public TediFile setUrl(String url) {
		this.url = url;
		return this;
	}

	public String getThumbUrl() {
		return thumbUrl;
	}

	public TediFile setThumbUrl(String thumbUrl) {
		this.thumbUrl = thumbUrl;
		return this;
	}

	public String getContentType() {
		return contentType;
	}

	public TediFile setContentType(String contentType) {
		this.contentType = contentType;
		return this;
	}
	
	
}
