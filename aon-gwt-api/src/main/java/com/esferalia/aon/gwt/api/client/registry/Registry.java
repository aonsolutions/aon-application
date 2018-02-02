package com.esferalia.aon.gwt.api.client.registry;

import com.esferalia.aon.gwt.api.client.IApi;
import com.esferalia.aon.gwt.api.client.IApiAsync;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.Methods;
import com.esferalia.aon.gwt.api.client.incidence.JsObject;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class Registry extends Methods{

	final IApiAsync impl = GWT.create(IApi.class);

	public Registry(String url, String accesToken, String domainName, Integer domainId, String userName) {
		this.url = url;
		this.accessToken = accesToken;
		this.domainName = domainName;
		this.domainId = domainId;
		this.userName = userName;
		this.scheme = url.contains("https") ? "https" : "http";
	}
	
	public void getCustomers(AsyncCallback<JSON<JsObject>> callback){
		get(getUrl() + "registry/"+getDomainName()+"/"+getUserName()+"/customer/", callback);
	}
	
	public void getSuppliers(AsyncCallback<JSON<JsObject>> callback){
		get(getUrl() + "registry/"+getDomainName()+"/"+getUserName()+"/supplier/", callback);
	}
	
	public void getSellers(AsyncCallback<JSON<JsObject>> callback){
		get(getUrl() + "registry/"+getDomainName()+"/"+getUserName()+"/seller/", callback);
	}
}
