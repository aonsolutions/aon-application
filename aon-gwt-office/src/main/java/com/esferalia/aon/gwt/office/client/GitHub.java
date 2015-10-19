package com.esferalia.aon.gwt.office.client;

import com.esferalia.aon.gwt.office.client.models.AJSON;
import com.esferalia.aon.gwt.office.client.models.JSON;
import com.esferalia.aon.gwt.office.client.models.issues.JsIssue;
import com.esferalia.aon.gwt.office.client.models.issues.JsIssueComment;
import com.esferalia.aon.gwt.office.client.models.issues.JsLabel;
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

public class GitHub implements GitHubService {

	private static String accessToken = null;
	private String baseUrl = "https://api.github.com/";
	private static boolean authorized = false;

	@Override
	public void setGitHubUrl(String url) {
		this.baseUrl = url;
	}

	@Override
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	@Override
	public boolean isAuthorized() {
		return this.isAuthorized();
	}

	// ************** USERS *************** //

	@Override
	public void getUser(String login, AsyncCallback<AJSON<JsUser>> callback) {
		get(baseUrl + "users/" + URL.encode(login), callback);
	}

	@Override
	public void getUser(AsyncCallback<AJSON<JsUser>> callback) {
		get(baseUrl + "user", callback);
	}

	// *********** REPOSITORIES *********** //

	@Override
	public void getRepos(AsyncCallback<JSON<JsRepo>> callback) {
		get(baseUrl + "user/repos", callback);
	}

	@Override
	public void getRepos(String user, AsyncCallback<JSON<JsRepo>> callback) {
		get(baseUrl + "users/" + URL.encode(user) + "/repos", callback);
	}

	@Override
	public void getRepo(String login, String name,
			AsyncCallback<AJSON<JsRepo>> callback) {
		get(baseUrl + "repos/" + URL.encode(login) + "/" + URL.encode(name),
				callback);
	}

	@Override
	public void saveRepo(JsRepo r, RepoValue prop,
			AsyncCallback<JsRepo> callback) {
		post(r.getUrl(), prop, callback);
	}

	// ************** ISSUES ************** //

	@Override
	public void getIssues(String user, String r,
			AsyncCallback<JSON<JsIssue>> callback) {
		get(baseUrl + "repos/" + user + "/" + r + "/issues", callback);
	}

	@Override
	public void getIssues(JsRepo r, AsyncCallback<JSON<JsIssue>> callback) {
		get(r.getUrl() + "/issues", callback);
	}

	@Override
	public void createIssue(JsRepo r, IssueValue prop,
			AsyncCallback<JsIssue> callback) {
		post(r.getUrl() + "/issues", prop, callback);
	}

	@Override
	public void editIssue(JsRepo r, JsIssue issue, IssueValue prop,
			AsyncCallback<JsIssue> callback) {

		if (issue == null)
			createIssue(r, prop, callback);
		else
			post(r.getUrl() + "/issues/" + issue.getNumber(), prop, callback);
	}

	// *************** COMMENTS ****************** //

	@Override
	public void getIssueComments(JsRepo r, JsIssue issue,
			AsyncCallback<JSON<JsIssueComment>> callback) {
		get(r.getUrl() + "/issues/" + issue.getNumber() + "/comments", callback);
	}

	@Override
	public void createIssueComment(JsRepo r, JsIssue issue,
			IssueCommentValue prop, AsyncCallback<JsIssueComment> callback) {
		post(r.getUrl() + "/issues/" + issue.getNumber() + "/comments", prop,
				callback);
	}

	// *************** LABELS ****************** //

	@Override
	public void getLabels(JsRepo repo, AsyncCallback<JSON<JsLabel>> callback) {
		get(repo.getUrl() + "/labels", callback);
	}

	@Override
	public void createLabel(JsRepo repo, LabelValue prop,
			AsyncCallback<JsLabel> callback) {
		post(repo.getUrl() + "/labels", prop, callback);
	}

	@Override
	public void saveLabel(JsRepo repo, String name, LabelValue prop,
			AsyncCallback<JsLabel> callback) {
		if (name == null)
			createLabel(repo, prop, callback);
		else
			post(repo.getUrl() + "/labels/" + URL.encode(name), prop, callback);
	}

	@Override
	public void saveLabel(JsRepo repo, JsLabel label, LabelValue prop,
			AsyncCallback<JsLabel> callback) {
		if (label == null)
			createLabel(repo, prop, callback);
		else
			post(repo.getUrl() + "/labels/" + URL.encode(label.getName()),
					prop, callback);
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
				callback.onFailure(caught);
			}
		};
	}

	public static final <T extends JavaScriptObject> void get(String url,
			final AsyncCallback<T> callback) {
		String requestUrl = makeRequestUrl(url);
		GWT.log("[GET]" + requestUrl);
		JsonpRequestBuilder jsonp = new JsonpRequestBuilder();
		jsonp.requestObject(requestUrl, hookCallback(callback));
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
