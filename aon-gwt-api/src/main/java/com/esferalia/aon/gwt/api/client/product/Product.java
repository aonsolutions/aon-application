package com.esferalia.aon.gwt.api.client.product;

import java.util.HashMap;
import java.util.LinkedList;

import com.esferalia.aon.gwt.api.client.IApi;
import com.esferalia.aon.gwt.api.client.IApiAsync;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.Methods;
import com.esferalia.aon.gwt.api.client.incidence.JsObject;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class Product extends Methods{

	final IApiAsync impl = GWT.create(IApi.class);

	public Product(String url, String accesToken, String domainName, Integer domainId, String userName) {
		this.url = url;
		this.accessToken = accesToken;
		this.domainName = domainName;
		this.domainId = domainId;
		this.userName = userName;
		this.scheme = url.contains("https") ? "https" : "http";
	}
	
	public void getProductCategories(AsyncCallback<JSON<JsObject>> callback){
		get(getUrl() + "product/"+getDomainName()+"/"+getUserName()+"/category/", callback);
	}
	
	public void getProductCategory(Integer id, AsyncCallback<JSON<JsObject>> callback){
		get(getUrl() + "product/"+getDomainName()+"/"+getUserName()+"/category/" + id, callback);
	}
	
	public void getItemList(HashMap<String, LinkedList<String>> filterMap, AsyncCallback<JSON<JsItem>> callback){
		String filter = filterMap.size() > 0 ? "?" + getFilter(filterMap) : "";
		get(getUrl() + "product/" + getDomainName() + "/" + getUserName() +"/item" + filter, callback);
	}
	
	public void getItem(Integer id, AsyncCallback<JSON<JsItem>> callback){
		get(getUrl() + "product/" + getDomainName() + "/" + getUserName() +"/item/"+id , callback);
	}
	
	public void insertItem( String requestData, AsyncCallback<JsItem> callback){
		post(getUrl() + "product/" + getDomainName() + "/" + getUserName() +"/item", requestData, callback);
	}
	
	public void getProductList(AsyncCallback<JSON<JsObject>> callback){
		get(getUrl() + "product/" + getDomainName() + "/" + getUserName() + "/product/", callback);
	}
	
	
}
