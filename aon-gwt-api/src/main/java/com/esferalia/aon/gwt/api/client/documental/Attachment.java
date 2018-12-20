package com.esferalia.aon.gwt.api.client.documental;

import java.util.HashMap;
import java.util.LinkedList;

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
	
	public void getAttach(String id, AsyncCallback<JSON<JsAttach>> callback){
		get(url + "attachment/" + getDomainName() + "/" + getUserName() + "/file/" + id, callback);
	}
	
	public void updateAttach(String id, String requestData, AsyncCallback<JsAttach> callback){
		post(url + "attachment/" + getDomainName() + "/" + getUserName() + "/file/" + id, requestData, callback);
	}
	
	public void getAttachList(HashMap<String, LinkedList<String>> filterMap, AsyncCallback<JSON<JsAttach>> callback){
		String filter = filterMap.size() > 0 ? "?" + getFilter(filterMap) : "";
		Window.alert("filter ondoren!");
		get(url + "attachment/" + getDomainName() + "/" + getUserName() + "/files" + filter, callback);
	}
	
	public void getSystemAttachList( AsyncCallback<JSON<JsAttach>> callback){
		get(url + "attachment/" + getDomainName() + "/" + getUserName() + "/files_system", callback);
	}
	
	public void getParentAttachList( AsyncCallback<JSON<JsAttach>> callback){
		get(url + "attachment/" + getDomainName() + "/" + getUserName() + "/files_parent", callback);
	}
	
	public void getCertificates( AsyncCallback<JSON<JsAttach>> callback){
		get(url + "attachment/" + getDomainName() + "/" + getUserName() + "/certificates", callback);
	}
	
	public void getCategories(AsyncCallback<JSON<JsLabel>> callback){
		get(url + "attachment/" + getDomainName() + "/" + getUserName() + "/category", callback);
	}	
	
	public void createCategory(String requestData, AsyncCallback<JsLabel> callback){
		post(url + "attachment/"+getDomainName()+"/"+getUserName()+"/category/create", requestData, callback);
	}
	
	public void updateCategory(JsLabel category, String requestData, AsyncCallback<JsLabel> callback){
		post(url + "attachment/"+getDomainName()+"/"+getUserName()+"/category/edit/"
				+ category.getId(), requestData, callback);
	}
	
	public void deleteCategory(JsLabel category, AsyncCallback<JsLabel> callback){
		post(url + "attachment/"+getDomainName()+"/"+getUserName()+"/category/delete/"
				+ category.getId(),  "{}", callback);
	}
		
	public void getTags(AsyncCallback<JSON<JsLabel>> callback){
		get(url + "attachment/" + getDomainName() + "/" + getUserName() + "/tag", callback);
	}	

	public void createTag(String requestData, AsyncCallback<JsLabel> callback){
		post(url + "attachment/"+getDomainName()+"/"+getUserName()+"/tag/create", requestData, callback);
	}
	
	public void updateTag(JsLabel tag, String requestData, AsyncCallback<JsLabel> callback){
		post(url + "attachment/"+getDomainName()+"/"+getUserName()+"/tag/edit/"
				+ tag.getId(), requestData, callback);
	}
	
	public void deleteTag(JsLabel tag, AsyncCallback<JsLabel> callback){
		post(url + "attachment/"+getDomainName()+"/"+getUserName()+"/tag/delete/"
				+ tag.getId(), "{}", callback);
	}
	
	public void getScopes(AsyncCallback<JSON<JsObject>> callback){
		get(url + "attachment/"+ getDomainName() + "/" + getUserName() + "/scope",callback);
	}	

	public void getQualityImages(Integer id,AsyncCallback<JSON<JsAttach>> callback){
		get(url + "attachment/" + getDomainName() + "/" + getUserName() + "/quality?id="+id, callback);
	}
	
	public void removeAttach(String requestData,AsyncCallback<JSON<JsAttach>> callback){
		post(url + "attachment/" + getDomainName() + "/" + getUserName() + "/remove", requestData, callback);
	}
	
	// -------------------- DOWNLOAD
	
	public void download(String id){	
		String str ="domain="+ getDomainId() + "&id=" + id + "&attach_type=registry";
		impl.base(str, new AsyncCallback<String>() {
			
			@Override
			public void onSuccess(String result) {
				Window.open(url + "download_attachment/"  + getDomainName() + "/" + getUserName() + "/" +  result, "_blank", null);
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
	}
	
	
	public void sendDocuments(String requestData){
		post(getUrl() + "documental_notification/" + getDomainName() + "/" + getUserName()  , requestData);
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
