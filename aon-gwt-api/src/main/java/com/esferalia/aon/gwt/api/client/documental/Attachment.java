package com.esferalia.aon.gwt.api.client.documental;

import com.esferalia.aon.gwt.api.client.AonUrlApi;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.Methods;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class Attachment extends Methods{
	
	String url;
	String userName;
	String domainName;
	
	public Attachment(AonUrlApi url, String accessToken) {
		this.url = url.getUrl();
		this.accessToken = accessToken;
	}
	
	public Attachment(AonUrlApi url, String accesToken, String userName, String domainName) {
		this.url = url.getUrl() + "aon-aio/";
		this.userName = userName;
		this.domainName = domainName;
		this.accessToken = accesToken;
		this.scheme = url.getUrl().contains("https") ? "https" : "http";
	}
	
	public Attachment(String url, String accesToken, String userName, String domainName) {
		this.url = url + "aon-aio/";
		this.userName = userName;
		this.domainName = domainName;
		this.accessToken = accesToken;
		this.scheme = url.contains("https") ? "https" : "http";
	}
	
	public void getA(String url, AsyncCallback<JSON<JavaScriptObject>> callback){
		get(url, callback);
	}
	
	public void getAttachList( AsyncCallback<JSON<JsAttach>> callback){
			get(url + "attachment/" + getDomainName() + "/" + getUserName() + "/", callback);
	}
	
	//---------------------- Métodos Get & Set
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}
}
