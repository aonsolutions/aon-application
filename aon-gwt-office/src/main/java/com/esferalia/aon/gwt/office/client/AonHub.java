package com.esferalia.aon.gwt.office.client;

import java.util.Date;

import com.esferalia.aon.gwt.office.client.models.AJSON;
import com.esferalia.aon.gwt.office.client.models.JSON;
import com.esferalia.aon.gwt.office.client.models.issues.JsIssue;
import com.esferalia.aon.gwt.office.client.models.issues.JsIssueComment;
import com.esferalia.aon.gwt.office.client.models.issues.JsLabel;
import com.esferalia.aon.gwt.office.client.models.repos.JsRMedia;
import com.esferalia.aon.gwt.office.client.models.repos.JsRegistry;
import com.esferalia.aon.gwt.office.client.models.repos.JsRepo;
import com.esferalia.aon.gwt.office.client.models.users.JsUser;
import com.esferalia.aon.gwt.office.client.values.IssueCommentValue;
import com.esferalia.aon.gwt.office.client.values.LabelControlValue;
import com.esferalia.aon.gwt.office.client.values.LabelValue;
import com.esferalia.aon.gwt.office.client.values.RepoValue;
import com.esferalia.aon.gwt.office.client.values.Value;
import com.esferalia.aon.gwt.office.client.values.issues.IssueValue;
import com.esferalia.aon.occam.api.model.office.Notice;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.jsonp.client.JsonpRequestBuilder;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class AonHub {

	private static String accessToken = null;
	private static String baseUrl = "https://api.github.com/";
	private static String repositoryUrl = "https://api.github.com/";
	private static String since = null;
	private static String sender = null;
	
	private static String filterTagList = null;
	private static String filterUser = null;
	private static String text;
	
	private static int offset = 0;
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

	public void setSinceCriteria(Date since) {
		DateTimeFormat fmt = DateTimeFormat.getFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		try {
			this.since = fmt.format(since);	
		} catch (Exception ex) {
			this.since = null;
		}
	}
	
	public void setOffset(int offset) {
		this.offset = offset;
	}
	
	public void setSender(String sender) {
		this.sender = sender;
	}
	
	public void setFilterTagList(String[] filterTag) {
		if (filterTag == null) 
			this.filterTagList = null;
		else {
			String aux = makeNamesString(filterTag);
			this.filterTagList = aux;
		}
	}
	
	public void setFilterUserList(String filterUser) {
		if (filterUser == null)
			this.filterUser = null;
		else {			
			this.filterUser = filterUser;
		}
	}
	
	public void setText(String text) {
		this.text = text;
	}

	// ************** USERS *************** //

	public void getUser(String login, String userName, AsyncCallback<AJSON<JsUser>> callback) {
		get(baseUrl + "users/" + URL.encode(login) + "/" + URL.encode(userName), callback);
	}
	
	public void getUsers(String login, AsyncCallback<JSON<JsUser>> callback) {
		get(baseUrl + "users/" + URL.encode(login), callback);
	}

	// *********** REPOSITORIES *********** //

	public void createRepository(RepoValue prop,
			AsyncCallback<JsRepo> callback) {
		post(baseUrl + "user/repos", prop, callback);
	}

	public void getRepoOrganization(String organization,
			AsyncCallback<JSON<JsRepo>> callback) {
		get(baseUrl + "orgs/" + organization + "/repos", callback);
	}

	public void getRepos(String user, AsyncCallback<JSON<JsRepo>> callback) {
		get(baseUrl + "users/" + URL.encode(user) + "/repos", callback);
	}

	public void getRepo(String login, String name,
			AsyncCallback<AJSON<JsRepo>> callback) {
		get(baseUrl + "repos/" + URL.encode(login) + "/" + URL.encode(name),
				callback);
	}

	public void saveRepo(RepoValue prop, AsyncCallback<JsRepo> callback) {
		post(repositoryUrl, prop, callback);
	}

	public void deleteRepository(AsyncCallback<JsRepo> callback) {
		delete(repositoryUrl, callback);
	}

	// ************** ISSUES ************** //

	/**
	 * para tratar el metodo desde una api, los parametros deben ser los
	 * siguientes: String user: Id del dominio. String repo: Nombre del dominio.
	 */

	public void getOpenIssues(String user, String repo,
			AsyncCallback<JSON<JsIssue>> callback) {
		get(baseUrl + "repos/" + user + "/" + repo + "/issues?state=open"
				+ (since != null ? "&since=" + this.since : "")
				+ (sender != null ? "&sender=" + URL.encode(sender) : "")
				+ "&offset=" + offset
				+ (filterTagList != null ? "&labels=" + URL.encode(filterTagList) : "")				
				+ (text != null ? "&text=" + URL.encode(text) : "")
				+ (filterUser != null ? "&user=" + URL.encode(filterUser) : ""), callback);
	}

	public void getClosedIssues(String user, String repo,
			AsyncCallback<JSON<JsIssue>> callback) {
		get(baseUrl + "repos/" + user + "/" + repo + "/issues?state=closed"
				+ (since != null ? "&since=" + this.since : "")
				+ (sender != null ? "&sender=" + URL.encode(sender) : "")
				+ "&offset=" + offset
				+ (filterTagList != null ? "&labels=" + URL.encode(filterTagList) : "")				
				+ (text != null ? "&text=" + URL.encode(text) : "")
				+ (filterUser != null ? "&users=" + URL.encode(filterUser) : ""), callback);
	}

	public void getAllIssues(String user, String repo,
			AsyncCallback<JSON<JsIssue>> callback) {
		get(baseUrl + "repos/" + user + "/" + repo + "/issues?state=all"
				+ (since != null ? "&since=" + this.since : "")
				+ (sender != null ? "&sender=" + URL.encode(sender) : "")
				+ "&offset=" + offset
				+ (filterTagList != null ? "&labels=" + URL.encode(filterTagList) : "")				
				+ (text != null ? "&text=" + URL.encode(text) : "")
				+ (filterUser != null ? "&users=" + URL.encode(filterUser) : ""), callback);
	}

	public void createIssue(String user, String repo, IssueValue prop,
			AsyncCallback<JsIssue> callback) {
		post(baseUrl + "repos/" + user + "/" + repo + "/issues", prop,
				callback);
	}
	
	public void addDuplicateNotice(String user, String repo, int parentId, 
			IssueValue prop, AsyncCallback<JsIssue> callback) {
		post(baseUrl + "repos/" + user + "/" + repo + "/issues/duplicated"
				+ "/" + parentId, prop, callback);
	}

	public void editIssue(String user, String repo, JsIssue issue,
			IssueValue prop, AsyncCallback<JsIssue> callback) {

		if (issue == null)
			createIssue(user, repo, prop, callback);
		else
			post(baseUrl + "repos/" + user + "/" + repo + "/issues/"
					+ issue.getNumber(), prop, callback);
	}

	public void saveNotice(String url, IssueValue prop,
			AsyncCallback<JsIssue> callback) {
		post(url, prop, callback);

	}

	// *************** COMMENTS ****************** //

	public void getIssueComments(String user, String repo, JsIssue issue,
			AsyncCallback<JSON<JsIssueComment>> callback) {
		get(baseUrl + "repos/" + user + "/" + repo + "/issues/"
				+ issue.getNumber() + "/comments", callback);
	}

	public void createIssueComment(String user, String repo, JsIssue issue,
			IssueCommentValue prop, AsyncCallback<JsIssueComment> callback) {
		post(baseUrl + "repos/" + user + "/" + repo + "/issues/"
				+ issue.getNumber() + "/comments", prop, callback);
	}

	public void editIssueComment(String user, String repo, Integer id,
			IssueCommentValue prop, AsyncCallback<JsIssueComment> callback) {
		post(baseUrl + "repos/" + user + "/" + repo + "/issues/comments/" + id,
				prop, callback);
	}

	public void deleteIssueComment(String user, String repo, Integer id,
			AsyncCallback<JsIssue> callback) {
		delete(baseUrl + "repos/" + user + "/" + repo + "/issues/comments/"
				+ id, callback);

	}

	// *************** LABELS ****************** //

	public void getLabels(String user, String repo,
			AsyncCallback<JSON<JsLabel>> callback) {
		get(baseUrl + "repos/" + user + "/" + repo + "/labels", callback);
	}

	public void createLabel(String user, String repo, LabelValue prop,
			AsyncCallback<JsLabel> callback) {
		post(baseUrl + "repos/" + user + "/" + repo + "/", prop, callback);
	}

	public void addLabelsToAnIssue(String user, String repo, Integer issueId,
			IssueValue prop, AsyncCallback<JsIssue> callback) {
		post(baseUrl + "repos/" + user + "/" + repo + "/issues/" + issueId
				+ "/labels/", prop, callback);
	}

	public void replaceLabelsForIssue(String user, String repo, Integer issueId,
			LabelControlValue prop, AsyncCallback<JsIssue> callback) {
		post(baseUrl + "repos/" + user + "/" + repo + "/notice/" + issueId
				+ "/labels/", prop, callback);
	}

	public void removeLabelFromIssue(String user, String repo, Integer issueId,
			String labelName, AsyncCallback<JsLabel> callback) {
		delete(baseUrl + "repos/" + user + "/" + repo + "/issues/" + issueId
				+ "/labels/" + URL.encode(labelName), callback);
	}

	public void saveLabel(String user, String repo, String name,
			LabelValue prop, AsyncCallback<JsLabel> callback) {
		if (name == null)
			createLabel(user, repo, prop, callback);
		else
			post(baseUrl + "repos/" + user + "/" + repo + "/labels/"
					+ URL.encode(name), prop, callback);
	}

	public void saveLabel(String user, String repo, JsLabel label,
			LabelValue prop, AsyncCallback<JsLabel> callback) {
		if (label == null)
			createLabel(user, repo, prop, callback);
		else
			post(baseUrl + "repos/" + user + "/" + repo + "/labels/"
					+ URL.encode(label.getName()), prop, callback);
	}

	public void deleteLabel(String user, String repo, String labelName,
			AsyncCallback<JsLabel> callback) {
		delete(baseUrl + "repos/" + user + "/" + repo + "/labels/"
				+ URL.encode(labelName), callback);
	}

	// *************** REGISTRIES ****************** //
	/**
	 * 
	 * @param domain
	 *            id del current domain
	 * @param domainName
	 *            nombre del current domain
	 * @param callback
	 */
	public void getRegistries(String user, String repo,
			AsyncCallback<JSON<JsRegistry>> callback) {
		get(baseUrl + "repos/" + user + "/" + repo + "/registries", callback);
	}
	
	public void getRMedias(String user, String repo,
			AsyncCallback<JSON<JsRMedia>> callback) {
		get(baseUrl + "repos/" + user + "/" + repo + "/rmedia", callback);
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

		if (baseUrl.contains("api.github.com")) {
			// API GITHUB
			GWT.log("[GET] " + requestUrl);
			JsonpRequestBuilder jsonp = new JsonpRequestBuilder();
			jsonp.requestObject(requestUrl, hookCallback(callback));
		} else {
			// OTRA URL
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

	private <T extends JavaScriptObject> void post(String url, Value<?> request,
			AsyncCallback<T> callback) {

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
		RequestBuilder builder = new RequestBuilder(RequestBuilder.DELETE,
				requestUrl);
		final AsyncCallback<T> hookedCallback = hookCallback(callback);
		final StringBuilder log = new StringBuilder();
		log.append(" [DELETE] ---> " + requestUrl);

		try {
			builder.sendRequest(null, new RequestCallback() {

				@Override
				public void onResponseReceived(Request request,
						Response response) {
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

		} catch (RequestException ex) {
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
	
	private static String makeNamesString(String[] names) {
		StringBuilder sb = new StringBuilder();
		for (int x = 0; x < names.length; x++) {
			sb.append(names[x]);
			sb.append(',');
		}
		return sb.toString().substring(0, sb.length() - 1);
	}
}
