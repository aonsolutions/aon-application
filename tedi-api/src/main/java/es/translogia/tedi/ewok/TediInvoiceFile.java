package es.translogia.tedi.ewok;

import java.io.Serializable;

public class TediInvoiceFile implements Serializable {

	private static final long serialVersionUID = 7774880156315910737L;
	
	private String url;
	private String thumbUrl;
	private String contentType;

	public String getUrl() {
		return url;
	}

	public TediInvoiceFile setUrl(String url) {
		this.url = url;
		return this;
	}

	public String getThumbUrl() {
		return thumbUrl;
	}

	public TediInvoiceFile setThumbUrl(String thumbUrl) {
		this.thumbUrl = thumbUrl;
		return this;
	}

	public String getContentType() {
		return contentType;
	}

	public TediInvoiceFile setContentType(String contentType) {
		this.contentType = contentType;
		return this;
	}

}
