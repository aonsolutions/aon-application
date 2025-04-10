package net.aonsolutions.aws.atc.lambda;


public class MIModelo420Request {
	
    private Object headers;
    private boolean isBase64Encoded;
    private String rawPath;
    private String routeKey;
    private Object requestContext;
    private String body;
    private String version;
    private String rawQueryString;
	
    
    public Object getHeaders() {
		return headers;
	}
	public void setHeaders(Object headers) {
		this.headers = headers;
	}
	
	public boolean isBase64Encoded() {
		return isBase64Encoded;
	}
	public void setBase64Encoded(boolean isBase64Encoded) {
		this.isBase64Encoded = isBase64Encoded;
	}
	
	public String getRawPath() {
		return rawPath;
	}
	public void setRawPath(String rawPath) {
		this.rawPath = rawPath;
	}
	
	public String getRouteKey() {
		return routeKey;
	}
	public void setRouteKey(String routeKey) {
		this.routeKey = routeKey;
	}
	
	public Object getRequestContext() {
		return requestContext;
	}
	public void setRequestContext(Object requestContext) {
		this.requestContext = requestContext;
	}
	
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	
	public String getRawQueryString() {
		return rawQueryString;
	}
	public void setRawQueryString(String rawQueryString) {
		this.rawQueryString = rawQueryString;
	}

}
