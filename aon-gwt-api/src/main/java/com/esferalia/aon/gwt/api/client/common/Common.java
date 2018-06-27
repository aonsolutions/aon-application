package com.esferalia.aon.gwt.api.client.common;

import java.util.HashMap;
import java.util.LinkedList;

import com.esferalia.aon.gwt.api.client.AonUrlApi;
import com.esferalia.aon.gwt.api.client.IApi;
import com.esferalia.aon.gwt.api.client.IApiAsync;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.Methods;
import com.esferalia.aon.gwt.api.client.incidence.JsObject;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class Common extends Methods{

	final IApiAsync impl = GWT.create(IApi.class);
	
	public Common(String url, String accesToken, String domainName, Integer domainId, String userName) {
		this.url = url;
		this.accessToken = accesToken;
		this.domainName = domainName;
		this.domainId = domainId;
		this.userName = userName;
		this.scheme = url.contains("https") ? "https" : "http";
	}
	
	public void getWorkplaces(AsyncCallback<JSON<JsObject>> callback){
		get(getUrl() + "common/workplace", callback);
	}
	
	public void getMailAccounts(AsyncCallback<JSON<JsObject>> callback){
		get(getUrl() + "common/mail_account", callback);
	}
	
	public void getSignatures(AsyncCallback<JSON<JsObject>> callback){
		get(getUrl() + "common/signature", callback);
	}
	
	
	// ------------------- APPLICATION PARAMETER (app_param)
	
	public void getAppParam(String appParam, AsyncCallback<JSON<JsAppParam>> callback){
		get(getUrl() + "common/app_param?param="+appParam, callback);		
	}
	
	public void insertAppParam(String requestData){
		post(getUrl()+ "common/"+getDomainName()+"/"+getUserName()+"/app_param", requestData);
	}
	
	public void insertAppParam(String requestData, AsyncCallback<JsAppParam> callback){
		post(getUrl()+ "common/"+getDomainName()+"/"+getUserName()+"/app_param", requestData, callback);
	}
		
	public void deleteAppParam(String requestData){
		post(getUrl()+ "common/"+getDomainName()+"/"+getUserName()+"/app_param/delete", requestData);
	}
	
	public void deleteAppParam(String requestData, AsyncCallback<JsAppParam> callback){
		post(getUrl()+ "common/"+getDomainName()+"/"+getUserName()+"/app_param/delete", requestData, callback);
	}
	
	// ------------------- DATA RESPONSE (data_response)
	
	public void getDataResponseJS(HashMap<String, LinkedList<String>> filterMap, AsyncCallback<JSON<JsDataResponse>> callback){
		String filter = filterMap.size() > 0 ? "?" + getFilter(filterMap) : "";
		get(AonUrlApi.AON.getUrl() + "domains/" + getDomainName() + "/dataResponse" + filter, callback);
	}
	
	public void getDataResponse(HashMap<String, LinkedList<String>> filterMap, AsyncCallback<JSON<JsDataResponse>> callback){
		String filter = filterMap.size() > 0 ? "?" + getFilter(filterMap) : "";
		get(getUrl() + "common/data_response" + filter, callback);
	}
	
	public void getDataResponseQuality(HashMap<String, LinkedList<String>> filterMap, AsyncCallback<JSON<JsDataResponse>> callback){
		String filter = filterMap.size() > 0 ? "?" + getFilter(filterMap) : "";
		get(getUrl() + "quality/"+getDomainName()+"/"+getUserName()+"/data_response" + filter, callback);
	}
	
	public void getDataResponsePaturpatQuality(HashMap<String, LinkedList<String>> filterMap, AsyncCallback<JSON<JsDataResponse>> callback){
		String filter = filterMap.size() > 0 ? "?" + getFilter(filterMap) : "";
		get(getUrl() + "quality/"+getDomainName()+"/"+getUserName()+"/paturpat" + filter, callback);
	}
	
	public void insertDataResponse(String requestData, AsyncCallback<JsDataResponse> callback){
		post(getUrl()+ "common/"+getDomainName()+"/"+getUserName()+"/data_response", requestData, callback);
	}
	
	public void insertDataResponse2(String requestData, AsyncCallback<JsDataResponse> callback){
		post(getUrl()+ "common/"+getDomainName()+"/"+getUserName()+"/data_response2", requestData, callback);
	}
	
	public void insertDataResponseDetail(String requestData){
		post(getUrl()+ "common/"+getDomainName()+"/"+getUserName()+"/data_response/detail", requestData);
	}
	
}
