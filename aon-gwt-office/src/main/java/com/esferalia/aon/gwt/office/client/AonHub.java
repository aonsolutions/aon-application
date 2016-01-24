package com.esferalia.aon.gwt.office.client;

import com.esferalia.aon.gwt.office.client.models.AJSON;
import com.esferalia.aon.gwt.office.client.models.JSON;
import com.esferalia.aon.gwt.office.client.models.issues.JsIssue;
import com.esferalia.aon.gwt.office.client.models.issues.JsIssueComment;
import com.esferalia.aon.gwt.office.client.models.issues.JsLabel;
import com.esferalia.aon.gwt.office.client.models.repos.JsRegistry;
import com.esferalia.aon.gwt.office.client.models.repos.JsRepo;
import com.esferalia.aon.gwt.office.client.models.users.JsUser;
import com.esferalia.aon.gwt.office.client.values.IssueCommentValue;
import com.esferalia.aon.gwt.office.client.values.LabelValue;
import com.esferalia.aon.gwt.office.client.values.RepoValue;
import com.esferalia.aon.gwt.office.client.values.Value;
import com.esferalia.aon.gwt.office.client.values.issues.IssueValue;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.jsonp.client.JsonpRequestBuilder;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class AonHub implements IAonHub {

	private static String accessToken = null;
	private static String baseUrl = "https://api.github.com/";
	private static String repositoryUrl = "https://api.github.com/";
	private static boolean authorized = false;
	
	public AonHub(String url) {
		this.baseUrl = url;
	}	
	
	public void setRepositoryUrl(String repositoryUrl) {
		this.repositoryUrl = repositoryUrl;
	}
	
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	
	public boolean isAuthorized() {
		return this.isAuthorized();
	}
	
	// ************** USERS *************** //

	@Override
	public void getUser(String login, AsyncCallback<AJSON<JsUser>> callback) {
		get(baseUrl + "users/" + URL.encode(login), callback);
	}
	

	// *********** REPOSITORIES *********** //

	@Override	
	public void createRepository(RepoValue prop, AsyncCallback<JsRepo> callback) {
		post(baseUrl + "user/repos", prop, callback);
	}
	
	@Override
	public void getRepoOrganization(String organization,
			AsyncCallback<JSON<JsRepo>> callback) {
		get(baseUrl + "orgs/"+ organization + "/repos", callback);
	}

	@Override
	public void getRepos(String user, AsyncCallback<JSON<JsRepo>> callback) {
		get(baseUrl + "users/" + URL.encode(user) + "/repos", callback);
	}

	@Override
	public void getRepo(String login, String name,
			AsyncCallback<AJSON<JsRepo>> callback) {
		get(baseUrl + "repos/" + URL.encode(login) + "/" + URL.encode(name), callback);
	}

	@Override
	public void saveRepo(RepoValue prop,
			AsyncCallback<JsRepo> callback) {
		post(repositoryUrl, prop, callback);
	}
	
	@Override
	public void deleteRepository(AsyncCallback<JsRepo> callback) {
		delete(repositoryUrl, callback);
	}

	// ************** ISSUES ************** //
	
	@Override
	public void getOpenIssues(String user, String repo,
			AsyncCallback<JSON<JsIssue>> callback) {
		get(baseUrl + "repos/" + user + "/" + repo + "/issues?state=open", callback);
	}
	
	@Override
	public void getClosedIssues(String user, String repo,
			AsyncCallback<JSON<JsIssue>> callback) {
		get(baseUrl + "repos/" + user + "/" + repo + "/issues?state=closed", callback);
	}
	
	@Override
	public void getAllIssues(String user, String repo,
			AsyncCallback<JSON<JsIssue>> callback) {
		get(baseUrl + "repos/" + user + "/" + repo + "/issues?state=all", callback);
	}

	@Override
	public void createIssue(IssueValue prop,
			AsyncCallback<JsIssue> callback) {
		post(repositoryUrl + "/issues", prop, callback);
	}

	@Override
	public void editIssue(JsIssue issue, IssueValue prop,
			AsyncCallback<JsIssue> callback) {

		if (issue == null)
			createIssue(prop, callback);
		else
			post(repositoryUrl + "/issues/" + issue.getNumber(), prop, callback);
	}
	
	public void saveNotice (String url, IssueValue prop, AsyncCallback<JsIssue> callback) {
		post(url, prop, callback);
	
	}
	
	public void addLabel2Issue (JsRepo repo, JsIssue issue, LabelValue prop, AsyncCallback<JsLabel> callback) {
		post(repo.getUrl() + "/issues/" + issue.getNumber() + "/labels", prop, callback);
	}
	
	@Override
	public void deleteIssue(JsIssue issue,
			AsyncCallback<JsIssue> callback) {
		delete(repositoryUrl + "/issues/" + issue.getNumber(), callback);
	}


	// *************** COMMENTS ****************** //

	@Override
	public void getIssueComments(JsIssue issue,
			AsyncCallback<JSON<JsIssueComment>> callback) {
		get(repositoryUrl + "/issues/" + issue.getNumber() + "/comments", callback);
	}

	@Override
	public void createIssueComment(JsIssue issue,
			IssueCommentValue prop, AsyncCallback<JsIssueComment> callback) {
		post(repositoryUrl + "/issues/" + issue.getNumber() + "/comments", prop,
				callback);
	}
	
	@Override
	public void editIssueComment(String user, String repo,
			Integer id, IssueCommentValue prop,
			AsyncCallback<JsIssueComment> callback) {
		post(repositoryUrl + "/repos/" + user + "/" + repo + "/issues/comments/" + id, prop, callback);
	}
	
	@Override
	public void deleteIssueComment(Integer id,
			AsyncCallback<JsIssue> callback) {
		delete(repositoryUrl + "/issues/comments/" + id, callback);
		
	}

	// *************** LABELS ****************** //

	@Override
	public void getLabels(AsyncCallback<JSON<JsLabel>> callback) {
		get(repositoryUrl + "/labels", callback);
	}

	@Override
	public void createLabel(LabelValue prop,
			AsyncCallback<JsLabel> callback) {
		post(repositoryUrl + "/labels", prop, callback);
	}
	
	
	@Override
	public void saveLabel(String name, LabelValue prop,
			AsyncCallback<JsLabel> callback) {
		if (name == null)
			createLabel(prop, callback);
		else
			post(repositoryUrl + "/labels/" + URL.encode(name), prop, callback);
	}

	@Override
	public void saveLabel(JsLabel label, LabelValue prop,
			AsyncCallback<JsLabel> callback) {
		if (label == null)
			createLabel(prop, callback);
		else
			post(repositoryUrl + "/labels/" + URL.encode(label.getName()),
					prop, callback);
	}
	
	@Override
	public void deleteLabel(String labelName,
			AsyncCallback<JsLabel> callback) {
		delete(repositoryUrl + "/labels/" + labelName, callback);
	}
	
	// *************** REGISTRIES ****************** //
	
	public void getRegistries(AsyncCallback<JSON<JsRegistry>> callback) {
		get(repositoryUrl + "/registries", callback);
	}

	
	// ********* PUBLIC STATIC METHODS *********** //

	private static <T extends JavaScriptObject> AsyncCallback<T> hookCallback(
			final AsyncCallback<T> callback) {
		return new AsyncCallback<T>() {
			@Override
			public void onSuccess(T result) {
				if (accessToken != null)
					authorized = true;
				callback.onSuccess(result);
			}

			@Override
			public void onFailure(Throwable caught) {
				System.out.println("Ha fallado : " + caught.getMessage());
				callback.onFailure(caught);
			}
		};
	}

	public static final <T extends JavaScriptObject> void get(String url,
			final AsyncCallback<T> callback) {

		String requestUrl = makeRequestUrl(url);
		
		if ( baseUrl.contains("api.github.com") ) {
			//API GITHUB
			GWT.log("[GET] " + requestUrl);
			JsonpRequestBuilder jsonp = new JsonpRequestBuilder();		
			jsonp.requestObject(requestUrl, hookCallback(callback));
		}
		else {
			//OTRA URL
			RequestBuilder builder = new RequestBuilder(RequestBuilder.GET,
					requestUrl);

			final AsyncCallback<T> hookedCallback = hookCallback(callback);
			final StringBuilder log = new StringBuilder();
			log.append("[GET]" + requestUrl);
			
			try {			
				builder.sendRequest(null, new RequestCallback() {
					@Override
					public void onResponseReceived(Request request,
							Response response) {
						
						T result = JsonUtils.<T> safeEval(response.getText());
						log.append("\n\n--" + response.getStatusText() + ":"
								+ response.getStatusCode() + "\n"
								+ response.getText());
						hookedCallback.onSuccess(result);
						GWT.log(log.toString());
					}

					@Override
					public void onError(Request request, Throwable e) {
						log.append("\n\n--" + e.getStackTrace());
						hookedCallback.onFailure(e);
						GWT.log(log.toString());
					}
				});
			} catch (RequestException e) {
				log.append("\n\n--" + e.getStackTrace());
				hookedCallback.onFailure(e);
				GWT.log(log.toString());
			}
		}
	}
	
	private <T extends JavaScriptObject> void post(String url,  
			Value<?> request, AsyncCallback<T> callback) {
			
		String requestUrl = makeRequestUrl(url); 
		RequestBuilder builder = new RequestBuilder(RequestBuilder.POST,
				requestUrl);

		String requestJson = request.toJson();
		final AsyncCallback<T> hookedCallback = hookCallback(callback);
		final StringBuilder log = new StringBuilder();
		log.append("[POST]" + requestUrl + "\n" + requestJson);
		
		try {			
			builder.sendRequest(requestJson, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request,
						Response response) {
					
					T result = JsonUtils.<T> safeEval(response.getText());
					log.append("\n\n--" + response.getStatusText() + ":"
							+ response.getStatusCode() + "\n"
							+ response.getText());
					hookedCallback.onSuccess(result);
					GWT.log(log.toString());
				}

				@Override
				public void onError(Request request, Throwable e) {
					log.append("\n\n--" + e.getStackTrace());
					hookedCallback.onFailure(e);
					GWT.log(log.toString());
				}
			});
		} catch (RequestException e) {
			log.append("\n\n--" + e.getStackTrace());
			hookedCallback.onFailure(e);
			GWT.log(log.toString());
		}
	}
	
	private <T extends JavaScriptObject> void delete(String url, 
			AsyncCallback<T> callback) {
		String requestUrl = makeRequestUrl(url); 		
		RequestBuilder builder = new RequestBuilder(RequestBuilder.DELETE, requestUrl);
		final AsyncCallback<T> hookedCallback = hookCallback(callback);
		final StringBuilder log = new StringBuilder();
		log.append(" [DELETE] ---> " + requestUrl);
		
		try {
			builder.sendRequest(null, new RequestCallback() {
				
				@Override
				public void onResponseReceived(Request request, Response response) {					
					log.append("\n\n--" + response.getStatusText() + ":"
							+ response.getStatusCode() + "\n"
							+ response.getText());
					hookedCallback.onSuccess(null);
					GWT.log(log.toString());
				}
				
				@Override
				public void onError(Request request, Throwable e) {
					log.append("\n\n--" + e.getStackTrace());
					hookedCallback.onFailure(e);
					GWT.log(log.toString());
				}
			});
			
		} catch ( RequestException ex) {
			log.append("\n\n--" + ex.getStackTrace());
			hookedCallback.onFailure(ex);
			GWT.log(log.toString());

		}
	}
	

	private static String makeRequestUrl(String url) {
		String prefix = "?";
		if (url.contains("?")) {
			prefix = "&";
		}

		if (accessToken != null) {
			url += prefix + "access_token=" + accessToken;
		}
		return url;
	}
}
