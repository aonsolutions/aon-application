package com.esferalia.aon.gwt.api.client.common;

import java.util.HashMap;
import java.util.LinkedList;

import com.esferalia.aon.gwt.api.client.AonUrlApi;
import com.esferalia.aon.gwt.api.client.IApi;
import com.esferalia.aon.gwt.api.client.IApiAsync;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.Methods;
import com.esferalia.aon.gwt.api.client.incidence.JsObject;
import com.esferalia.aon.gwt.api.client.incidence.JsUser;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
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

	public void getUsers(HashMap<String, LinkedList<String>> filterMap, AsyncCallback<JSON<JsUser>> callback){
		String filter = filterMap.size() > 0 ? "?" + getFilter(filterMap) : "";
		String url = getUrl() + "ms/user/" + getDomainName() + "/" + getUserName() + filter;
		get(url, callback);
	}
	
	public void getCompanies(HashMap<String, LinkedList<String>> filterMap, AsyncCallback<JSON<JsCompany>> callback){
		String filter = filterMap.size() > 0 ? "?" + getFilter(filterMap) : "";
		String url = getUrl() + "ms/company/" + getDomainName() + "/" + getUserName() + filter;
		get(url, callback);
	}
	
	public void generateCompanyScope(String requestData, AsyncCallback<JSON<JsCompany>> callback){
		String url = getUrl() + "ms/company/" + getDomainName() + "/" + getUserName() + "/generateScope/";
		post(url, requestData, callback);
	}
	
	public void deleteCompanyScope(Integer id, AsyncCallback<JSON<JsCompany>> callback){
		String url = getUrl() + "ms/company/" + getDomainName() + "/" + getUserName() + "/deleteScope/" + id;
		delete(url, "", callback);
	}
	
	public void copyUserScope(Integer user, Integer userId, AsyncCallback<JSON<JsUser>> callback){
		String url = getUrl() + "ms/user/" + getDomainName() + "/" + getUserName() + "/copyUserScope/" + user + "/" + userId;
		post(url, "", callback);
	}
	
	public void deleteUserScope(Integer user, Integer userId, AsyncCallback<JSON<JsUser>> callback) {
		String url = getUrl() + "ms/user/" + getDomainName() + "/" + getUserName() + "/deleteScope/" + user + "/" + userId;
		delete(url, "", callback);
	}
	
	public void getScopes(HashMap<String, LinkedList<String>> filterMap, AsyncCallback<JSON<JsObject>> callback){
		String filter = filterMap.size() > 0 ? "?" + getFilter(filterMap) : "";
		String url = getUrl() + "ms/scope/" + getDomainName() + "/" + getUserName() + filter;
		get(url, callback);
	}
	
	public void getScopeUsers(Integer scope, AsyncCallback<JSON<JsUser>> callback){
		String url = getUrl() + "ms/scope/" + getDomainName() + "/" + getUserName() + "/user/" + scope;
		get(url, callback);
	}
	
	public void getScopeCompanies(Integer scope, AsyncCallback<JSON<JsObject>> callback){
		String url = getUrl() + "ms/scope/" + getDomainName() + "/" + getUserName() + "/company/" + scope;
		get(url, callback);
	}
	
	public void addGroupScope(String requestData, AsyncCallback<JavaScriptObject> callback){
		post(getUrl()+ "ms/scope/"+getDomainName()+"/"+getUserName()+"/group", requestData, callback);
	}
	
	public void removeGroupScopes(String requestData, AsyncCallback<JavaScriptObject> callback){
		delete(getUrl()+ "ms/scope/"+getDomainName()+"/"+getUserName()+"/group", requestData, callback);
	}
	
	public void updateUserScope(String requestData, AsyncCallback<JavaScriptObject> callback){
		post(getUrl()+ "ms/scope/"+getDomainName()+"/"+getUserName()+"/user", requestData, callback);
	}
	
	public void removeUserScopes(String requestData, AsyncCallback<JavaScriptObject> callback){
		delete(getUrl()+ "ms/scope/"+getDomainName()+"/"+getUserName()+"/users", requestData, callback);
	}
	
	public void removeUserScope(String requestData, AsyncCallback<JavaScriptObject> callback){
		delete(getUrl()+ "ms/scope/"+getDomainName()+"/"+getUserName()+"/user", requestData, callback);
	}
	
	public void updateCompanyScope(String requestData, AsyncCallback<JavaScriptObject> callback){
		post(getUrl()+ "ms/scope/"+getDomainName()+"/"+getUserName()+"/company", requestData, callback);
	}
	
	public void removeCompanyScope(String requestData, AsyncCallback<JavaScriptObject> callback){
		delete(getUrl()+ "ms/scope/"+getDomainName()+"/"+getUserName()+"/company", requestData, callback);
	}
	
	
	public void getWorkplaces(AsyncCallback<JSON<JsObject>> callback){
		get(getUrl() + "ms/common/" + getDomainName() + "/" + getUserName() + "/workplace", callback);
	}
	
	public void getMailAccounts(AsyncCallback<JSON<JsObject>> callback){
		get(getUrl() + "ms/common/" + getDomainName() + "/" + getUserName() + "/mail_account", callback);
	}
	
	public void getSignatures(AsyncCallback<JSON<JsObject>> callback){
		get(getUrl() + "ms/common/" + getDomainName() + "/" + getUserName() + "/signature", callback);
	}
	
	
	// ------------------- APPLICATION PARAMETER (app_param)
	
	public void getAppParam(String appParam, AsyncCallback<JSON<JsAppParam>> callback){
		get(getUrl() + "ms/common/" + getDomainName() + "/" + getUserName() + "/app_param?param="+appParam, callback);		
	}
	
	public void insertAppParam(String requestData){
		post(getUrl()+ "ms/common/"+getDomainName()+"/"+getUserName()+"/app_param", requestData);
	}
	
	public void insertAppParam(String requestData, AsyncCallback<JsAppParam> callback){
		post(getUrl()+ "ms/common/"+getDomainName()+"/"+getUserName()+"/app_param", requestData, callback);
	}
		
	public void deleteAppParam(String requestData){
		post(getUrl()+ "ms/common/"+getDomainName()+"/"+getUserName()+"/app_param/delete", requestData);
	}
	
	public void deleteAppParam(String requestData, AsyncCallback<JsAppParam> callback){
		post(getUrl()+ "ms/common/"+getDomainName()+"/"+getUserName()+"/app_param/delete", requestData, callback);
	}
	
	// ------------------- DATA RESPONSE (data_response)
	
	public void getDataResponseJS(HashMap<String, LinkedList<String>> filterMap, AsyncCallback<JSON<JsDataResponse>> callback){
		String filter = filterMap.size() > 0 ? "?" + getFilter(filterMap) : "";
		get(AonUrlApi.AON.getUrl() + "domains/" + getDomainName() + "/dataResponse" + filter, callback);
	}
	
	public void getDataResponse(HashMap<String, LinkedList<String>> filterMap, AsyncCallback<JSON<JsDataResponse>> callback){
		String filter = filterMap.size() > 0 ? "?" + getFilter(filterMap) : "";
		get(getUrl() + "ms/common/" + getDomainName() + "/" + getUserName() + "/data_response" + filter, callback);
	}
	
	public void getDataResponseQuality(HashMap<String, LinkedList<String>> filterMap, AsyncCallback<JSON<JsDataResponse>> callback){
		String filter = filterMap.size() > 0 ? "?" + getFilter(filterMap) : "";
		get(getUrl() + "ms/quality/"+getDomainName()+"/"+getUserName()+"/data_response" + filter, callback);
	}
	
	public void getDataResponsePaturpatQuality(HashMap<String, LinkedList<String>> filterMap, AsyncCallback<JSON<JsDataResponse>> callback){
		String filter = filterMap.size() > 0 ? "?" + getFilter(filterMap) : "";
		get(getUrl() + "ms/quality/" + getDomainName() + "/" + getUserName() + "/paturpat" + filter, callback);
	}
	
	public void insertDataResponse(String requestData, AsyncCallback<JsDataResponse> callback){
		post(getUrl()+ "ms/common/" + getDomainName() + "/" + getUserName() + "/data_response", requestData, callback);
	}
	
	public void insertDataResponse2(String requestData, AsyncCallback<JsDataResponse> callback){
		post(getUrl()+ "ms/common/" + getDomainName() + "/" + getUserName() + "/data_response2", requestData, callback);
	}
	
	public void insertDataResponseDetail(String requestData){
		post(getUrl()+ "ms/common/" + getDomainName() + "/" + getUserName() + "/data_response/detail", requestData);
	}
	
	// -------------------- Selected menu... 
	
	public void selectedMenu(AsyncCallback<JSON<JsObject>> callback){
		get(getUrl() + "ms/common/" + getDomainName() + "/" + getUserName() + "/selectedMenu", callback);
	}
	
}
