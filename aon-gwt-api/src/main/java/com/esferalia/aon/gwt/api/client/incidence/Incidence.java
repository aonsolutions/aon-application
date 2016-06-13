package com.esferalia.aon.gwt.api.client.incidence;

import com.esferalia.aon.gwt.api.client.AonUrlApi;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.Methods;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class Incidence extends Methods{
	
	private AonUrlApi url;
	String user;
	String repository;
	
	public Incidence(AonUrlApi url) {
		this.url = url;
	}
	
	public Incidence(AonUrlApi url, String user, String repository) {
		this.url = url;
		this.user = user;
		this.repository = repository;
	}
	
	public void getAllIssues(String user, String repo, AsyncCallback<JSON<JsIssue>> callback){
		get(url.getUrl() + "repos/" + user + "/" + repo + "/issues?state=all", callback);
	}

	public void getClosedIssues(String user, String repo, AsyncCallback<JSON<JsIssue>> callback){
		get(url.getUrl() + "repos/" + user + "/" + repo + "/issues?state=closed", callback);	
	}
	
	public void getOpenIssues(String user, String repo, AsyncCallback<JSON<JsIssue>> callback) {
		get(url.getUrl() + "repos/" + user + "/" + repo + "/issues?state=open", callback);
	}

	public void createIssue(String user, String repo, String requestData,
			AsyncCallback<JsIssue> callback) {
		post(url.getUrl() + "repos/" + user + "/" + repo + "/issues", requestData, callback);
	}

	//---------------------- Métodos Get & Set
	
	public AonUrlApi getUrl() {
		return url;
	}

	public void setUrl(AonUrlApi url) {
		this.url = url;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getRepository() {
		return repository;
	}

	public void setRepository(String repository) {
		this.repository = repository;
	}
}
