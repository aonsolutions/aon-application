package com.esferalia.aon.gwt.api.client.fiscal;

import java.util.HashMap;
import java.util.LinkedList;

import com.esferalia.aon.gwt.api.client.IApi;
import com.esferalia.aon.gwt.api.client.IApiAsync;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.Methods;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class Fiscal extends Methods{

	final IApiAsync impl = GWT.create(IApi.class);

	public Fiscal(String url, String accesToken, String domainName, Integer domainId, String userName) {
		this.url = url;
		this.accessToken = accesToken;
		this.domainName = domainName;
		this.domainId = domainId;
		this.userName = userName;
		this.scheme = url.contains("https") ? "https" : "http";
	}
	
	public void getDepositConfiguration(AsyncCallback<JSON<JsDepositConfiguration>> callback){
		get(url + "ms/deposit/"+getDomainName()+"/"+getUserName()+"/configuration",callback);
	}
	
	public void setDepositConfiguration(String requestData){
		post(url + "ms/deposit/" + getDomainName() + "/" + getUserName() + "/configuration", requestData);
	}
	
	public void send2AEAT(String url, String requestData, AsyncCallback<JavaScriptObject> callback){
		post(url, requestData, callback);
	}

	public void download(String id){	
		String str ="domain="+ getDomainId() + "&id=" + id + "&attach_type=data";
		impl.base(str, new AsyncCallback<String>() {
			
			@Override
			public void onSuccess(String result) {
				Window.open(getUrl() + "ms/download_attachment/" + result, "_blank", null);
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
	}
	
	public void getFiscalModels(HashMap<String, LinkedList<String>> filterMap,AsyncCallback<JSON<JsFiscalMenuItem>> callback){
		String str = filterMap.size() > 0 ? "?" + getFilter(filterMap) : "";
		get(getUrl() + "ms/api/fiscal/"+getDomainName()+"/"+getUserName()+"/matrix" + str, callback, 30000);
	}
	
}
