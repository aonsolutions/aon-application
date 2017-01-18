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

	public Registry(String url, String accesToken, String domainName, String userName) {
		this.url = url;
		this.accessToken = accesToken;
		this.domainName = domainName;
		this.userName = userName;
	}
	
	public void getCustomers(AsyncCallback<JSON<JsObject>> callback){
		get(getUrl() + "registry/"+getDomainName()+"/"+getUserName()+"/customer/", callback);
	}
	
	public void getSellers(AsyncCallback<JSON<JsObject>> callback){
		get(getUrl() + "registry/"+getDomainName()+"/"+getUserName()+"/seller/", callback);
	}
}
