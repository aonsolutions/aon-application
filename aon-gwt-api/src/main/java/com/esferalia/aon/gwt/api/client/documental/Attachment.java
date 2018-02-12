package com.esferalia.aon.gwt.api.client.documental;

import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.Methods;
import com.esferalia.aon.gwt.api.client.incidence.JsLabel;
import com.esferalia.aon.gwt.api.client.incidence.JsObject;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class Attachment extends Methods{
	
	String url;
	String userName;
	String domainName;
	
	
	public Attachment(String url, String accesToken, String domainName, Integer domainId, String userName) {
		this.url = url;
		this.accessToken = accesToken;
		this.domainName = domainName;
		this.domainId = domainId;
		this.userName = userName;
		this.scheme = url.contains("https") ? "https" : "http";
	}
	
	public void getA(String url, AsyncCallback<JSON<JavaScriptObject>> callback){
		get(url, callback);
	}
	
	public void getAttachList( AsyncCallback<JSON<JsAttach>> callback){
		get(url + "attachment/files", callback);
	}
	
	public void getCertificates( AsyncCallback<JSON<JsAttach>> callback){
		get(url + "attachment/certificates", callback);
	}
	
	public void getCategories(AsyncCallback<JSON<JsLabel>> callback){
		get(url + "attachment/category",callback);
	}	
	
	public void getTags(AsyncCallback<JSON<JsLabel>> callback){
		get(url + "attachment/tag",callback);
	}	

	public void getScopes(AsyncCallback<JSON<JsObject>> callback){
		get(url + "attachment/scope",callback);
	}	

	public void getQualityImages(Integer id,AsyncCallback<JSON<JsAttach>> callback){
		get(url + "attachment/quality?id="+id, callback);
	}
	
	public void removeAttach(String requestData,AsyncCallback<JSON<JsAttach>> callback){
		post(url + "attachment/" + getDomainName() + "/" + getUserName() + "/remove", requestData, callback);
	}
	
	// -------------------- DOWNLOAD
	
	public void download(String id){	
		String str ="domain="+ getDomainId() + "&id=" + id;
		impl.base(str, new AsyncCallback<String>() {
			
			@Override
			public void onSuccess(String result) {
				Window.open(getUrl() + "download_attachment/" + result, "_blank", null);
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
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
