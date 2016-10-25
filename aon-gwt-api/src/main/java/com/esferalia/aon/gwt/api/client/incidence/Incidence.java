package com.esferalia.aon.gwt.api.client.incidence;

import com.esferalia.aon.gwt.api.client.AonUrlApi;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.Methods;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class Incidence extends Methods{
	
	String url;
	String userName;
	String repositoryName;
	String domainName;
	String organizationName;
	
	public Incidence(AonUrlApi url, String accessToken) {
		this.url = url.getUrl();
		this.accessToken = accessToken;
	}
	
	public Incidence(AonUrlApi url, String accesToken, String userName, String organizationName, String repositoryName) {
		this.url = url.getUrl(); //+ "aon-aio/";
		this.userName = userName;
		this.repositoryName = repositoryName;
		this.domainName = repositoryName;
		this.organizationName = organizationName;
		this.accessToken = accesToken;
	}
	
	public Incidence(String url, String accesToken, String userName, String organizationName, String repositoryName) {
		this.url = url; //+ "aon-aio/";
		this.userName = userName;
		this.repositoryName = repositoryName;
		this.domainName = repositoryName;
		this.organizationName = organizationName;
		this.accessToken = accesToken;
	}
	
	
	//-------------------- ORGANIZATIONS
	
	public void getOrganizations(AsyncCallback<JSON<JsOrganization>> callback){
		get(url+ "user/orgs",callback);
	}
	
	//-------------------- REPOSITORIES
	
	public void getUserRepository(AsyncCallback<JsRepository> callback){
		get(url + "repos/"+getUserName()+"/"+getRepositoryName(),callback);
	}
	
	public void getAllUserRepositories(AsyncCallback<JSON<JsRepository>> callback){
		get(url + "users/"+getUserName()+"/repos?type=all", callback);
	}
	
	public void getPublicUserRepositories(AsyncCallback<JSON<JsRepository>> callback){
		get(url + "users/"+getUserName()+"/repos?type=public", callback);
	}
	
	public void getPrivateUserRepositories(AsyncCallback<JSON<JsRepository>> callback){
		get(url + "users/"+getUserName()+"/repos?type=private", callback);
	}

	public void getOrgRepository(AsyncCallback<JSON<JsRepository>> callback){
		get(url + "repos/"+getOrganizationName()+"/"+getRepositoryName(),callback);
	}
	
	public void getAllOrgRepositories(AsyncCallback<JSON<JsRepository>> callback){
		get(url + "orgs/"+getOrganizationName()+"/repos?type=all", callback);
	}
	
	public void getPublicOrgRepositories(AsyncCallback<JSON<JsRepository>> callback){
		get(url + "orgs/"+getOrganizationName()+"/repos?type=public", callback);
	}
	
	public void getPrivateOrgRepositories(AsyncCallback<JSON<JsRepository>> callback){
		get(url + "orgs/"+getOrganizationName()+"/repos?type=private", callback);
	}
	
	public void createUserRepository(String requestData, AsyncCallback<JsRepository> callback){
		post(url + "user/repos", requestData, callback);
	}
	
	public void createOrgRepository(String requestData, AsyncCallback<JsRepository> callback){
		post(url + "orgs/"+ getOrganizationName() +"/repos", requestData, callback);
	}
	
	public void deleteRepository(AsyncCallback<JsRepository> callback){
		delete(url + getUserName() +"/"+getRepositoryName(), "{}",callback);
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
				+ (filter.getPriority() != null ? "&priority=" + filter.getPriority() : "")
				+ (filter.getDateDiff() != null ? "&date_diff=" + filter.getDateDiff() : "")
				+ (filter.getEnterprise() != null ? "&enterprise=" + filter.getEnterprise() : "")
				+ (filter.getType() != null ? "&type=" + filter.getType() : "")
				+ (filter.getSince()!= null ? "&since=" + filter.getSince() : "")
				+ "&page="+ filter.getPage()+ "&per_page="+ filter.getPerPage()
				, callback);
	}
	
	public void getUserIssues(IssueFilter filter, AsyncCallback<JSON<JsIssue>> callback){
		get(url + "repos/" + getUserName() + "/" + getRepositoryName() + "/issues"
				+ (filter.getState() != null ? "?state=" + filter.getState() : "?state=all")
				+ (filter.getMilestone() != null ? "&milestone=" + filter.getMilestone() : "")
				+ (filter.getAssignee() != null ? "&asignee=" + filter.getAssignee() : "")
				+ (filter.getCreator() != null ? "&creator=" + filter.getCreator() : "")
				+ (filter.getMentioned() != null ? "&mentioned=" + filter.getMentioned() : "")
				+ (filter.getLabels() != null ? "&labels=" + filter.getLabels() : "")
				+ (filter.getSort() != null ? "&sort=" + filter.getSort() : "")
				+ (filter.getDirection() != null ? "&direction=" + filter.getDirection() : "")
				+ (filter.getPriority() != null ? "&priority=" + filter.getPriority() : "")
				+ (filter.getDateDiff() != null ? "&date_diff=" + filter.getDateDiff() : "")
				+ (filter.getEnterprise() != null ? "&enterprise=" + filter.getEnterprise() : "")
				+ (filter.getType() != null ? "&type=" + filter.getType() : "")
				+ (filter.getSince()!= null ? "&since=" + filter.getSince() : "")
				+ "&page="+ filter.getPage()+ "&per_page="+ filter.getPerPage()
				, callback);
	}
	
	public void getOrgIssues(IssueFilter filter, AsyncCallback<JSON<JsIssue>> callback){
		get(url + "repos/" + getOrganizationName() + "/" + getRepositoryName() + "/issues"
				+ (filter.getState() != null ? "?state=" + filter.getState() : "?state=all")
				+ (filter.getMilestone() != null ? "&milestone=" + filter.getMilestone() : "")
				+ (filter.getTitle() != null ? "&title=" + filter.getTitle() : "")
				+ (filter.getAssignee() != null ? "&asignee=" + filter.getAssignee() : "")
				+ (filter.getCreator() != null ? "&creator=" + filter.getCreator() : "")
				+ (filter.getMentioned() != null ? "&mentioned=" + filter.getMentioned() : "")
				+ (filter.getLabels() != null ? "&labels=" + filter.getLabels() : "")
				+ (filter.getSort() != null ? "&sort=" + filter.getSort() : "")
				+ (filter.getDirection() != null ? "&direction=" + filter.getDirection() : "")
				+ (filter.getPriority() != null ? "&priority=" + filter.getPriority() : "")
				+ (filter.getDateDiff() != null ? "&date_diff=" + filter.getDateDiff() : "")
				+ (filter.getEnterprise() != null ? "&enterprise=" + filter.getEnterprise() : "")
				+ (filter.getType() != null ? "&type=" + filter.getType() : "")
				+ (filter.getSince()!= null ? "&since=" + filter.getSince() : "")
				+ "&page="+ filter.getPage()+ "&per_page="+ filter.getPerPage()
				, callback);
	}
	
	public void getLightIssues(Integer id, IssueFilter filter, AsyncCallback<JSON<JsIssue>> callback){
		get(url + "repos/" + getOrganizationName() + "/" + getRepositoryName() + "/issues_light/" + id
				+ (filter.getState() != null ? "?state=" + filter.getState() : "?state=all")
				+ (filter.getMilestone() != null ? "&milestone=" + filter.getMilestone() : "")
				+ (filter.getTitle() != null ? "&title=" + filter.getTitle() : "")
				+ (filter.getAssignee() != null ? "&asignee=" + filter.getAssignee() : "")
				+ (filter.getCreator() != null ? "&creator=" + filter.getCreator() : "")
				+ (filter.getMentioned() != null ? "&mentioned=" + filter.getMentioned() : "")
				+ (filter.getLabels() != null ? "&labels=" + filter.getLabels() : "")
				+ (filter.getSort() != null ? "&sort=" + filter.getSort() : "")
				+ (filter.getDirection() != null ? "&direction=" + filter.getDirection() : "")
				+ (filter.getPriority() != null ? "&priority=" + filter.getPriority() : "")
				+ (filter.getDateDiff() != null ? "&date_diff=" + filter.getDateDiff() : "")
				+ (filter.getEnterprise() != null ? "&enterprise=" + filter.getEnterprise() : "")
				+ (filter.getType() != null ? "&type=" + filter.getType() : "")
				+ (filter.getSince()!= null ? "&since=" + filter.getSince() : "")
				+ "&page="+ filter.getPage()+ "&per_page="+ filter.getPerPage()
				, callback);
	}
	
	public void getDuplicateIssues(Integer parent, AsyncCallback<JSON<JsIssue>> callback){
		get(url + "repos/" + getOrganizationName() + "/" + getRepositoryName() + "/duplicates/"+parent, callback);
	}
	
	public void createUserIssue(String requestData,	AsyncCallback<JsIssue> callback) {
		post(url + "repos/" + getUserName() + "/" + getRepositoryName() + "/issues", requestData, callback);
	}
	
	public void createOrgIssue(String requestData, AsyncCallback<JsIssue> callback) {
		post(url + "repos/" + getOrganizationName() + "/" + getRepositoryName() + "/issues", requestData, callback);
	}
	
	public void updateUserIssue(JsIssue issue, String requestData, AsyncCallback<JsIssue> callback){
		post(url + "repos/" + getUserName() + "/" + getRepositoryName() + "/issues/"
						+ issue.getNumber(), requestData, callback);
	}
	
	public void updateOrgIssue(JsIssue issue, String requestData, AsyncCallback<JsIssue> callback) {
		post(url + "repos/" + getOrganizationName() + "/" + getRepositoryName() + "/issues/"
						+ issue.getNumber(), requestData, callback);
	}

	//-------------------- LABELS
	
	public void getLabels(AsyncCallback<JSON<JsLabel>> callback){
		get(url + "repos/"+getOrganizationName()+"/"+getRepositoryName()+"/labels",callback);
	}	
	
	public void getLabels(String filter, AsyncCallback<JSON<JsLabel>> callback){
		get(url + "repos/"+getOrganizationName()+"/"+getRepositoryName()+"/labels?filter=" + filter,callback);
	}	
	
	public void createLabel(String requestData, AsyncCallback<JsLabel> callback){
		post(url + "repos/"+getOrganizationName()+"/"+getRepositoryName()+"/labels", requestData, callback);
	}	
	
	public void updateLabel(JsLabel label, String requestData, AsyncCallback<JsLabel> callback){
		post(url + "repos/"+getOrganizationName()+"/"+getRepositoryName()+"/labels/"
				+ label.getName(), requestData, callback);
	}	
	
	public void deleteLabel(JsLabel label, AsyncCallback<JsLabel> callback){
		post(url + "delete/repos/"+getOrganizationName()+"/"+getRepositoryName()+"/labels/"
				+ label.getName(), "{}", callback);
	}
	
	public void addLabel2Issue(String name, Integer number, AsyncCallback<JsLabel> callback){
		post(url + "repos/"+getOrganizationName()+"/"+getRepositoryName()+"/issues/"+number+"/labels/"+name, "{}", callback);
	}
	
	public void deleteLabel2Issue(String name, Integer number, AsyncCallback<JsLabel> callback){
		post(url + "delete/repos/"+getOrganizationName()+"/"+getRepositoryName()+"/issues/"+number+"/labels/"+name, "{}", callback);
	}
	
	//-------------------- TYPES
	
	public void getTypes(AsyncCallback<JSON<JsLabel>> callback){
		get(url + "repos/"+getOrganizationName()+"/"+getRepositoryName()+"/types",callback);
	}	
	
	public void getTypes(String filter, AsyncCallback<JSON<JsLabel>> callback){
		get(url + "repos/"+getOrganizationName()+"/"+getRepositoryName()+"/types?filter=" + filter,callback);
	}	
	
	public void createType(String requestData, AsyncCallback<JsLabel> callback){
		post(url + "repos/"+getOrganizationName()+"/"+getRepositoryName()+"/types", requestData, callback);
	}	
	
	public void updateType(JsLabel label, String requestData, AsyncCallback<JsLabel> callback){
		post(url + "repos/"+getOrganizationName()+"/"+getRepositoryName()+"/types/"
				+ label.getName(), requestData, callback);
	}	
	
	public void deleteType(JsLabel label, AsyncCallback<JsLabel> callback){
		post(url + "delete/repos/"+getOrganizationName()+"/"+getRepositoryName()+"/types/"
				+ label.getName(), "{}", callback);
	}
	
	public void addType2Issue(String name, Integer number, AsyncCallback<JsLabel> callback){
		post(url + "repos/"+getOrganizationName()+"/"+getRepositoryName()+"/issues/"+number+"/type/"+name, "{}", callback);
	}
	
	public void deleteType2Issue(Integer number, AsyncCallback<JsLabel> callback){
		post(url + "delete/repos/"+getOrganizationName()+"/"+getRepositoryName()+"/issues/"+number+"/type/", "{}",callback);
	}
	
	//-------------------- PRIORITIES
	
	public void getPriorities(AsyncCallback<JSON<JsLabel>> callback){
		get(url+ "repos/"+getOrganizationName()+"/"+getRepositoryName()+"/priorities",callback);
	}	
	
	public void getPriorities(String filter, AsyncCallback<JSON<JsLabel>> callback){
		get(url + "repos/"+getOrganizationName()+"/"+getRepositoryName()+"/priorities?filter=" + filter,callback);
	}	
	
	public void createPriority(String requestData, AsyncCallback<JsLabel> callback){
		post(url + "repos/"+getOrganizationName()+"/"+getRepositoryName()+"/priorities", requestData, callback);
	}	

	public void updatePriority(JsLabel label, String requestData, AsyncCallback<JsLabel> callback){
		post(url + "repos/"+getOrganizationName()+"/"+getRepositoryName()+"/priorities/"
				+ label.getName(), requestData, callback);
	}	
	
	public void deletePriority(JsLabel label, AsyncCallback<JsLabel> callback){
		post(url + "delete/repos/"+getOrganizationName()+"/"+getRepositoryName()+"/priorities/"
				+ label.getName(), "{}", callback);
	}
	
	public void addPriority2Issue(String name, Integer number, AsyncCallback<JsLabel> callback){
		post(url + "repos/"+getOrganizationName()+"/"+getRepositoryName()+"/issues/"+number+"/priority/"+name, "{}", callback);
	}
	
	public void deletePriority2Issue(Integer number, AsyncCallback<JsLabel> callback){
		post(url + "delete/repos/"+getOrganizationName()+"/"+getRepositoryName()+"/issues/"+number+"/priority/", "{}",callback);
	}
	
	//-------------------- USERS
	
	public void getUsers(AsyncCallback<JSON<JsUser>> callback){
		get(url + "orgs/"+getOrganizationName()+"/"+getRepositoryName()+ "/members",callback);
		//GITHUB get(url.getUrl() + "orgs/"+getOrganizationName()+ "/members",callback);
	}	
	
	public void getUsers(JsUser workgroup, AsyncCallback<JSON<JsUser>> callback){
		if(workgroup != null && workgroup.getId() != null)
			get(url + "orgs/"+getOrganizationName()+"/"+getRepositoryName()+ "/members?w="+ workgroup.getId(),callback);
		else getUsers(callback);
		//GITHUB get(url.getUrl() + "orgs/"+getOrganizationName()+ "/members",callback);
	}	
	
	public void getApplicationUsers(AsyncCallback<JSON<JsUser>> callback){
		get(url + "orgs/"+getOrganizationName()+"/"+getRepositoryName()+ "/app_users",callback);
		//GITHUB get(url.getUrl() + "orgs/"+getOrganizationName()+ "/members",callback);
	}	
	
	public void getUsers(String filter, AsyncCallback<JSON<JsUser>> callback){
		get(url + "orgs/"+getOrganizationName()+"/"+getRepositoryName()+"/members?filter=" + filter,callback);
		//GITHUB get(url.getUrl() + "orgs/"+getOrganizationName()+"/members?filter=" + filter,callback);
	}
	
	public void getWorkgroups(AsyncCallback<JSON<JsUser>> callback){
		get(url + "orgs/"+getOrganizationName()+"/"+getRepositoryName()+"/workgroups",callback);
	}	
	
	public void getWorkgroups(String filter, AsyncCallback<JSON<JsUser>> callback){
		get(url + "orgs/"+getOrganizationName()+"/"+getRepositoryName()+"/workgroups?filter=" + filter, callback);
	}	
	
	public void addUser2Issue(Integer id, Integer number, AsyncCallback<JsUser> callback){
		post(url + "repos/"+getOrganizationName()+"/"+getRepositoryName()+"/issues/"+number+"/user/"+id, "", callback);
	}
	
	public void deleteUser2Issue(Integer number, AsyncCallback<JsUser> callback){
		post(url + "delete/repos/"+getOrganizationName()+"/"+getRepositoryName()+"/issues/"+number+"/user", "{}", callback);
	}
	
	public void addWorkgroup2Issue(Integer id, Integer number, AsyncCallback<JsUser> callback){
		post(url + "repos/"+getOrganizationName()+"/"+getRepositoryName()+"/issues/"+number+"/workgroup/"+id, "", callback);
	}
	
	public void deleteWorkgroup2Issue(Integer number, AsyncCallback<JsUser> callback){
		post(url + "delete/repos/"+getOrganizationName()+"/"+getRepositoryName()+"/issues/"+number+"/workgroup/", "{}", callback);
	}
	
	//-------------------- EVENTS
	
	public void getEvents(String url, AsyncCallback<JSON<JsEvent>> callback){
		get(url, callback);
	}
	
	//-------------------- COMMENTS
	
	public void getComments(String url, AsyncCallback<JSON<JsComment>> callback){
		get(url, callback);
	}
	
	public void newComment(JsIssue issue, String requestData, AsyncCallback<JsComment> callback){
		post(url + "repos/"+getOrganizationName()+"/"+getRepositoryName()+"/issues/"
				+ issue.getNumber() + "/comments", requestData, callback);
	}
	
	public void updateComment(JsIssue issue, JsComment comment, String requestData, AsyncCallback<JsComment> callback){
		post(url + "repos/"+getOrganizationName()+"/"+getRepositoryName()+"/issues/"
				+ issue.getNumber() + "/comments/" + comment.getId(), requestData, callback);
	}
	
	//-------------------- REGISTRIES
	
	public void getRegistries(AsyncCallback<JSON<JsUser>> callback){
		get(url + "repos/"+getOrganizationName()+"/"+getRepositoryName()+"/registries",callback);
	}
	
	public void getRegistries(String value, AsyncCallback<JSON<JsUser>> callback){
		get(url + "repos/"+getOrganizationName()+"/"+getRepositoryName()+"/registries?filter=" + value,callback);
	}
	
	//-------------------- NOTIFICATIONS
	
	public void getNotificationInfo(AsyncCallback<JSON<JsNotify>> callback){
		get(url + "notification/"+ getOrganizationName() + "/" + getRepositoryName(), callback);
	}	
	
	public void updateNotificationInfo(String requestData){
		post(url + "notification/"+ getOrganizationName() + "/" + getRepositoryName() + "/configuration", requestData);
	}

	public void sendNotification(JsIssue issue, String requestData){
		post(url + "notification/"+ getOrganizationName() + "/" + getRepositoryName(), requestData);
	}
		
	//-------------------- TASK
	/**
	 * 
	 * @param callback
	 */
	public void getTask(Integer id, AsyncCallback<JSON<JsIssue>> callback){
		get(url + "repos/" + getUserName() + "/" + getDomainName() + "/issues/" + id, callback);
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
	
	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}
}
