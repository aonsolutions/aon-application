package com.esferalia.aon.gwt.api.client.incidence;

import com.esferalia.aon.gwt.api.client.AonUrlApi;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.Methods;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class Incidence extends Methods{
	
	private AonUrlApi url;
	String userName;
	String repositoryName;
	String organizationName;
	
	public Incidence(AonUrlApi url, String accessToken) {
		this.url = url;
		this.accessToken = accessToken;
	}
	
	public Incidence(AonUrlApi url, String accesToken, String userName, String organizationName, String repositoryName) {
		this.url = url;
		this.userName = userName;
		this.repositoryName = repositoryName;
		this.organizationName = organizationName;
		this.accessToken = accesToken;
	}
	
	
	//-------------------- ORGANIZATIONS
	
	public void getOrganizations(AsyncCallback<JSON<JsOrganization>> callback){
		get(url.getUrl()+ "user/orgs",callback);
	}
	
	//-------------------- REPOSITORIES
	
	public void getUserRepository(AsyncCallback<JsRepository> callback){
		get(url.getUrl() + "repos/"+getUserName()+"/"+getRepositoryName(),callback);
	}
	
	public void getAllUserRepositories(AsyncCallback<JSON<JsRepository>> callback){
		get(url.getUrl() + "users/"+getUserName()+"/repos?type=all", callback);
	}
	
	public void getPublicUserRepositories(AsyncCallback<JSON<JsRepository>> callback){
		get(url.getUrl() + "users/"+getUserName()+"/repos?type=public", callback);
	}
	
	public void getPrivateUserRepositories(AsyncCallback<JSON<JsRepository>> callback){
		get(url.getUrl() + "users/"+getUserName()+"/repos?type=private", callback);
	}

	public void getOrgRepository(AsyncCallback<JSON<JsRepository>> callback){
		get(url.getUrl() + "repos/"+getOrganizationName()+"/"+getRepositoryName(),callback);
	}
	
	public void getAllOrgRepositories(AsyncCallback<JSON<JsRepository>> callback){
		get(url.getUrl() + "orgs/"+getOrganizationName()+"/repos?type=all", callback);
	}
	
	public void getPublicOrgRepositories(AsyncCallback<JSON<JsRepository>> callback){
		get(url.getUrl() + "orgs/"+getOrganizationName()+"/repos?type=public", callback);
	}
	
	public void getPrivateOrgRepositories(AsyncCallback<JSON<JsRepository>> callback){
		get(url.getUrl() + "orgs/"+getOrganizationName()+"/repos?type=private", callback);
	}
	
	public void createUserRepository(String requestData, AsyncCallback<JsRepository> callback){
		post(url.getUrl() + "user/repos", requestData, callback);
	}
	
	public void createOrgRepository(String requestData, AsyncCallback<JsRepository> callback){
		post(url.getUrl() + "orgs/"+ getOrganizationName() +"/repos", requestData, callback);
	}
	
	public void deleteRepository(AsyncCallback<JsRepository> callback){
		delete(url.getUrl() + getUserName() +"/"+getRepositoryName(), callback);
	}
	
	//-------------------- ISSUES
	
	public void getIssues(String url, IssueFilter filter, AsyncCallback<JSON<JsIssue>> callback){
		get(url + (filter.getState() != null ? "?state=" + filter.getState() : "?state=all")
				+ (filter.getMilestone() != null ? "&milestone=" + filter.getMilestone() : "")
				+ (filter.getAssignee() != null ? "&asignee=" + filter.getAssignee() : "")
				+ (filter.getCreator() != null ? "&creator=" + filter.getCreator() : "")
				+ (filter.getMentioned() != null ? "&mentioned=" + filter.getMentioned() : "")
				+ (filter.getLabels() != null ? "&labels=" + filter.getLabels() : "")
				+ (filter.getSort() != null ? "&sort=" + filter.getSort() : "")
				+ (filter.getDirection() != null ? "&direction=" + filter.getDirection() : "")
				+ (filter.getSince()!= null ? "&since=" + filter.getSince() : "")
				+ "&page="+ filter.getPage()+ "&per_page="+ filter.getPerPage()
				, callback);
	}
	
	public void getUserIssues(IssueFilter filter, AsyncCallback<JSON<JsIssue>> callback){
		get(url.getUrl() + "repos/" + getUserName() + "/" + getRepositoryName() + "/issues"
				+ (filter.getState() != null ? "?state=" + filter.getState() : "?state=all")
				+ (filter.getMilestone() != null ? "&milestone=" + filter.getMilestone() : "")
				+ (filter.getAssignee() != null ? "&asignee=" + filter.getAssignee() : "")
				+ (filter.getCreator() != null ? "&creator=" + filter.getCreator() : "")
				+ (filter.getMentioned() != null ? "&mentioned=" + filter.getMentioned() : "")
				+ (filter.getLabels() != null ? "&labels=" + filter.getLabels() : "")
				+ (filter.getSort() != null ? "&sort=" + filter.getSort() : "")
				+ (filter.getDirection() != null ? "&direction=" + filter.getDirection() : "")
				+ (filter.getSince()!= null ? "&since=" + filter.getSince() : "")
				+ "&page="+ filter.getPage()+ "&per_page="+ filter.getPerPage()
				, callback);
	}
	
	public void getOrgIssues(IssueFilter filter, AsyncCallback<JSON<JsIssue>> callback){
		get(url.getUrl() + "repos/" + getOrganizationName() + "/" + getRepositoryName() + "/issues"
				+ (filter.getState() != null ? "?state=" + filter.getState() : "?state=all")
				+ (filter.getMilestone() != null ? "&milestone=" + filter.getMilestone() : "")
				+ (filter.getAssignee() != null ? "&asignee=" + filter.getAssignee() : "")
				+ (filter.getCreator() != null ? "&creator=" + filter.getCreator() : "")
				+ (filter.getMentioned() != null ? "&mentioned=" + filter.getMentioned() : "")
				+ (filter.getLabels() != null ? "&labels=" + filter.getLabels() : "")
				+ (filter.getSort() != null ? "&sort=" + filter.getSort() : "")
				+ (filter.getDirection() != null ? "&direction=" + filter.getDirection() : "")
				+ (filter.getSince()!= null ? "&since=" + filter.getSince() : "")
				+ "&page="+ filter.getPage()+ "&per_page="+ filter.getPerPage()
				, callback);
	}
	
	public void createUserIssue(String requestData,	AsyncCallback<JsIssue> callback) {
		post(url.getUrl() + "repos/" + getUserName() + "/" + getRepositoryName() + "/issues", requestData, callback);
	}
	
	public void createOrgIssue(String requestData, AsyncCallback<JsIssue> callback) {
		post(url.getUrl() + "repos/" + getOrganizationName() + "/" + getRepositoryName() + "/issues", requestData, callback);
	}
	
	public void updateUserIssue(JsIssue issue, String requestData, AsyncCallback<JsIssue> callback){
		post(url.getUrl() + "repos/" + getUserName() + "/" + getRepositoryName() + "/issues/"
						+ issue.getNumber(), requestData, callback);
	}

	//-------------------- LABELS
	
	public void getLabels(AsyncCallback<JSON<JsLabel>> callback){
		get(url.getUrl() + "repos/"+getOrganizationName()+"/"+getRepositoryName()+"/labels",callback);
	}	
	
	public void createLabel(String requestData, AsyncCallback<JsLabel> callback){
		post(url.getUrl() + "repos/"+getOrganizationName()+"/"+getRepositoryName()+"/labels", requestData, callback);
	}	
	
	public void updateLabel(JsLabel label, String requestData, AsyncCallback<JsLabel> callback){
		post(url.getUrl() + "repos/"+getOrganizationName()+"/"+getRepositoryName()+"/labels"
				+ label.getName(), requestData, callback);
	}	
	
	public void deleteLabel(JsLabel label, AsyncCallback<JsLabel> callback){
		delete(url.getUrl() + "repos/"+getOrganizationName()+"/"+getRepositoryName()+"/labels"
				+ label.getName(), callback);
	}
	
	//-------------------- EVENTS
	
	public void getEvents(String url, AsyncCallback<JSON<JsEvent>> callback){
		get(url, callback);
	}
	
	//---------------------- Métodos Get & Set
	
	public AonUrlApi getUrl() {
		return url;
	}

	public void setUrl(AonUrlApi url) {
		this.url = url;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getRepositoryName() {
		return repositoryName;
	}

	public void setRepositoryName(String repositoryName) {
		this.repositoryName = repositoryName;
	}

	public String getOrganizationName() {
		return organizationName;
	}

	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}
	
	
	
}
