package net.aonsolutions.invofox.model;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class OCREndpoint implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private List<String> headers;
	private String method;
	private String url;
	
	public List<String> getHeaders() {
		if(headers == null)
			headers = new LinkedList<>();
		return headers;
	}
	
	public OCREndpoint setHeaders(List<String> headers) {
		this.headers = headers;
		return this;
	}

	public String getMethod() {
		return method;
	}
	
	public OCREndpoint setMethod(String method) {
		this.method = method;
		return this;
	}
	
	public String getUrl() {
		return url;
	}
	
	public OCREndpoint setUrl(String url) {
		this.url = url;
		return this;
	}
	
}
