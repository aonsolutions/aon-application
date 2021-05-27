package es.translogia.tedi.ewok;

import java.io.Serializable;

public class TediFile implements Serializable {

	private static final long serialVersionUID = 7774880156315910737L;
	
	private Integer id;
	private String attachType;
	private String url;
	private String thumbUrl;
	private String contentType;

	public Integer getId() {
		return id;
	}
	
	public TediFile setId(Integer id) {
		this.id = id;
		return this;
	}
	
	public String getAttachType() {
		return attachType;
	}
	
	public TediFile setAttachType(String attachType) {
		this.attachType = attachType;
		return this;
	}
	
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
