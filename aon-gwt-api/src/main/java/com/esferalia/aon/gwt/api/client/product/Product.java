package com.esferalia.aon.gwt.api.client.product;

import com.esferalia.aon.gwt.api.client.IApi;
import com.esferalia.aon.gwt.api.client.IApiAsync;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.Methods;
import com.esferalia.aon.gwt.api.client.incidence.JsObject;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class Product extends Methods{

	final IApiAsync impl = GWT.create(IApi.class);

	public Product(String url, String accesToken, String domainName, String userName) {
		this.url = url;
		this.accessToken = accesToken;
		this.domainName = domainName;
		this.userName = userName;
	}
	
	public void getProductCategories(AsyncCallback<JSON<JsObject>> callback){
		get(getUrl() + "product/"+getDomainName()+"/"+getUserName()+"/category/", callback);
	}
	
	public void getProductCategory(Integer id, AsyncCallback<JSON<JsObject>> callback){
		get(getUrl() + "product/"+getDomainName()+"/"+getUserName()+"/category/" + id, callback);
	}
	
}
