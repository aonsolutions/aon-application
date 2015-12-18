package com.esferalia.aon.gwt.office.client;

import static com.esferalia.aon.gwt.office.shared.ActionEnum.addcomment;
import static com.esferalia.aon.gwt.office.shared.ActionEnum.addissuelabel;
import static com.esferalia.aon.gwt.office.shared.ActionEnum.addrepo;
import static com.esferalia.aon.gwt.office.shared.ActionEnum.createissue;
import static com.esferalia.aon.gwt.office.shared.ActionEnum.createlabel;
import static com.esferalia.aon.gwt.office.shared.ActionEnum.deletecomment;
import static com.esferalia.aon.gwt.office.shared.ActionEnum.deleteissue;
import static com.esferalia.aon.gwt.office.shared.ActionEnum.deletelabel;
import static com.esferalia.aon.gwt.office.shared.ActionEnum.editcomment;
import static com.esferalia.aon.gwt.office.shared.ActionEnum.editissue;
import static com.esferalia.aon.gwt.office.shared.ActionEnum.getcomments;
import static com.esferalia.aon.gwt.office.shared.ActionEnum.loadallissues;
import static com.esferalia.aon.gwt.office.shared.ActionEnum.loadclosedissues;
import static com.esferalia.aon.gwt.office.shared.ActionEnum.loadlabels;
import static com.esferalia.aon.gwt.office.shared.ActionEnum.loadnamerepo;
import static com.esferalia.aon.gwt.office.shared.ActionEnum.loadopenissues;
import static com.esferalia.aon.gwt.office.shared.ActionEnum.loadorgrepo;
import static com.esferalia.aon.gwt.office.shared.ActionEnum.loaduser;
import static com.esferalia.aon.gwt.office.shared.ActionEnum.loaduserepo;
import static com.esferalia.aon.gwt.office.shared.ActionEnum.savelabel;

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
import com.esferalia.aon.gwt.office.shared.ActionEnum;
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
	private String baseUrl = "https://api.github.com/";
	private String repositoryUrl = "https://api.github.com/";
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
		get(baseUrl + "users/" + URL.encode(login), loaduser.get(), callback);
	}

	// *********** REPOSITORIES *********** //

	@Override	
	public void createRepository(RepoValue prop, AsyncCallback<JsRepo> callback) {
		post(baseUrl + "user/repos", prop, addrepo.get(), callback);
	}
	
	@Override
	public void getRepoOrganization(String organization,
			AsyncCallback<JSON<JsRepo>> callback) {
		get(baseUrl + "orgs/"+ organization + "/repos", loadorgrepo.get(), callback);
	}

	@Override
	public void getRepos(String user, AsyncCallback<JSON<JsRepo>> callback) {
		get(baseUrl + "users/" + URL.encode(user) + "/repos", loaduserepo.get(), callback);
	}

	@Override
	public void getRepo(String login, String name,
			AsyncCallback<AJSON<JsRepo>> callback) {
		get(baseUrl + "repos/" + URL.encode(login) + "/" + URL.encode(name), loadnamerepo.get(),
				callback);
	}

	@Override
	public void saveRepo(RepoValue prop,
			AsyncCallback<JsRepo> callback) {
		post(repositoryUrl, prop, ActionEnum.saverepo.get(), callback);
	}
	
	@Override
	public void deleteRepository(AsyncCallback<JsRepo> callback) {
		delete(repositoryUrl, ActionEnum.deleterepo.get(), callback);
	}

	// ************** ISSUES ************** //
	
	@Override
	public void getOpenIssues(String user, String repo,
			AsyncCallback<JSON<JsIssue>> callback) {
		get(baseUrl + "repos/" + user + "/" + repo + "/issues?state=open", loadopenissues.get(), callback);
	}
	
	@Override
	public void getClosedIssues(String user, String repo,
			AsyncCallback<JSON<JsIssue>> callback) {
		get(baseUrl + "repos/" + user + "/" + repo + "/issues?state=closed", loadclosedissues.get(), callback);
	}
	
	@Override
	public void getAllIssues(String user, String repo,
			AsyncCallback<JSON<JsIssue>> callback) {
		get(baseUrl + "repos/" + user + "/" + repo + "/issues?state=all", loadallissues.get(), callback);
	}

	@Deprecated
	@Override
	public void getIssues(String user, String r,
			AsyncCallback<JSON<JsIssue>> callback) {
		get(baseUrl + "repos/" + user + "/" + r + "/issues", "", callback);
	}
	
	@Deprecated
	@Override
	public void getIssues(JsRepo r, AsyncCallback<JSON<JsIssue>> callback) {
		get(r.getUrl() + "/issues", "", callback);
	}

	@Override
	public void createIssue(IssueValue prop,
			AsyncCallback<JsIssue> callback) {
		post(repositoryUrl + "/issues", prop, createissue.get(), callback);
	}

	@Override
	public void editIssue(JsIssue issue, IssueValue prop,
			AsyncCallback<JsIssue> callback) {

		if (issue == null)
			createIssue(prop, callback);
		else
			post(repositoryUrl + "/issues/" + issue.getNumber(), prop, editissue.get(), callback);
	}
	
	public void saveNotice (String url, IssueValue prop, AsyncCallback<JsIssue> callback) {
		post(url, prop, "saveNotice", callback);
	}
	
	public void addLabel2Issue (JsRepo repo, JsIssue issue, LabelValue prop, AsyncCallback<JsLabel> callback) {
		post(repo.getUrl() + "/issues/" + issue.getNumber() + "/labels", prop, addissuelabel.get(), callback);
	}
	
	@Override
	public void deleteIssue(String name,
			AsyncCallback<JsIssue> callback) {
		delete(repositoryUrl + "/labels/" + name, deleteissue.get(), callback);
	}


	// *************** COMMENTS ****************** //

	@Override
	public void getIssueComments(JsIssue issue,
			AsyncCallback<JSON<JsIssueComment>> callback) {
		get(repositoryUrl + "/issues/" + issue.getNumber() + "/comments", getcomments.get(), callback);
	}

	@Override
	public void createIssueComment(JsIssue issue,
			IssueCommentValue prop, AsyncCallback<JsIssueComment> callback) {
		post(repositoryUrl + "/issues/" + issue.getNumber() + "/comments", prop,
				addcomment.get(), callback);
	}
	
	@Override
	public void editIssueComment(String user, String repo,
			Integer id, IssueCommentValue prop,
			AsyncCallback<JsIssueComment> callback) {
		post(repositoryUrl + "/repos/" + user + "/" + repo + "/issues/comments/" + id, prop, editcomment.get(), callback);
	}
	
	@Override
	public void deleteIssueComment(Integer id,
			AsyncCallback<JsIssue> callback) {
		delete(repositoryUrl + "/issues/comments/" + id, deletecomment.get(), callback);
		
	}

	// *************** LABELS ****************** //

	@Override
	public void getLabels(AsyncCallback<JSON<JsLabel>> callback) {
		get(repositoryUrl + "/labels", loadlabels.get(), callback);
	}

	@Override
	public void createLabel(LabelValue prop,
			AsyncCallback<JsLabel> callback) {
		post(repositoryUrl + "/labels", prop, createlabel.get(), callback);
	}
	
	
	@Override
	public void saveLabel(String name, LabelValue prop,
			AsyncCallback<JsLabel> callback) {
		if (name == null)
			createLabel(prop, callback);
		else
			post(repositoryUrl + "/labels/" + URL.encode(name), prop, savelabel.get(), callback);
	}

	@Override
	public void saveLabel(JsLabel label, LabelValue prop,
			AsyncCallback<JsLabel> callback) {
		if (label == null)
			createLabel(prop, callback);
		else
			post(repositoryUrl + "/labels/" + URL.encode(label.getName()),
					prop, savelabel.get(), callback);
	}
	
	@Override
	public void deleteLabel(String labelName,
			AsyncCallback<JsLabel> callback) {
		delete(repositoryUrl + "/labels/" + labelName, deletelabel.get(), callback);
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

	public static final <T extends JavaScriptObject> void get(String url, String action,
			final AsyncCallback<T> callback) {		
		//String requestUrl = makeRequestUrl(url) + "&action="+action;
		String requestUrl = makeRequestUrl(url) + URL.encode("&action").concat("=").concat(URL.encode(action));		
		GWT.log("[GET]" + requestUrl);
		JsonpRequestBuilder jsonp = new JsonpRequestBuilder();		
		jsonp.requestObject(requestUrl, hookCallback(callback));		
	}
	
	private <T extends JavaScriptObject> void post(String url,
			Value<?> request, String action, AsyncCallback<T> callback) {
		
		StringBuffer buffer = new StringBuffer();
		buffer.append(URL.encode("action")).append('=').append(URL.encode(action));
		
		//String requestUrl = makeRequestUrl(url) + "&action="+action;
		String requestUrl = makeRequestUrl(url); //+ URL.encode("&action").concat("=").concat(URL.encode(action));
		
		RequestBuilder builder = new RequestBuilder(RequestBuilder.POST,
				requestUrl);
		builder.setHeader("Content-Type","application/x-www-form-urlencoded");

		String requestJson = request.toJson();
		final AsyncCallback<T> hookedCallback = hookCallback(callback);
		final StringBuilder log = new StringBuilder();
		log.append("[POST]" + requestUrl + "\n" + requestJson);
		try {			
			builder.sendRequest((baseUrl.contains("api.github")) ? requestJson : buffer.toString(), new RequestCallback() {
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
	
	private <T extends JavaScriptObject> void delete(String url, String action,
			AsyncCallback<T> callback) {
		String requestUrl = makeRequestUrl(url) + "&action=" + action;		
		RequestBuilder builder = new RequestBuilder(RequestBuilder.DELETE, requestUrl);
		final AsyncCallback<T> hookedCallback = hookCallback(callback);
		final StringBuilder log = new StringBuilder();
		log.append(" [DELETE] ---> " + requestUrl);
		try {
			builder.sendRequest(null, new RequestCallback() {
				
				@Override
				public void onResponseReceived(Request request, Response response) {					
					//T result = JsonUtils.<T> safeEval(response.getText());
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
