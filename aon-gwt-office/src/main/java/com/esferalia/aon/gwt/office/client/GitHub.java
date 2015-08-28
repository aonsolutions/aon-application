package com.esferalia.aon.gwt.office.client;

import com.esferalia.aon.gwt.office.client.models.AJSON;
import com.esferalia.aon.gwt.office.client.models.JSON;
import com.esferalia.aon.gwt.office.client.models.issues.Issue;
import com.esferalia.aon.gwt.office.client.models.repos.Repo;
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

public class GitHub {
	
	private String accessToken = null;
	private String baseUrl = "https://api.github.com/";
	private boolean authorized = false;
	
	public void setGitHubUrl (String url) {
		this.baseUrl = url;
	}
	
	public void setAccessToken (String accessToken) {
		this.accessToken = accessToken;
	}
	
	public boolean isAuthorized () {
		return this.authorized;
	}
	
	// *********** REPOSITORIES ***********
	
    public void getRepos(AsyncCallback<JSON<Repo>> callback) {
        get(baseUrl + "user/repos", callback);
    }

    public void getRepos(String user, AsyncCallback<JSON<Repo>> callback) {
        get(baseUrl + "users/" + URL.encode(user) + "/repos", callback);
    }

    public void getRepo(String login, String name, AsyncCallback<AJSON<Repo>> callback) {
        get(baseUrl + "repos/" + URL.encode(login) + "/" + URL.encode(name), callback);
    }
    
    public void saveRepo(Repo r, RepoValue prop, AsyncCallback<Repo> callback) {
        post(r.getUrl(), prop, callback);
    }

	
	// ************** ISSUES **************
	
    public void getIssues(String user, String r, AsyncCallback<JSON<Issue>> callback) {
        get(baseUrl + "repos/" + user + "/" + r + "/issues", callback);
    }

    public void getIssues(Repo r, AsyncCallback<JSON<Issue>> callback) {
        get(r.getUrl() + "/issues", callback);
    }

    public void createIssue(Repo r, IssueValue prop, final AsyncCallback<Issue> callback) {
        post(r.getUrl() + "/issues", prop, callback);
    }

    public void editIssue(Repo r, Issue issue, IssueValue prop,
            final AsyncCallback<Issue> callback) {
        if (issue == null) {
            createIssue(r, prop, callback);
        } else {
            post(r.getUrl() + "/issues/" + issue.getNumber(), prop, callback);
        }
    }
	
	
    // ********* PRIVATE METHODS ***********
    
    private <T extends JavaScriptObject> AsyncCallback<T> hookCallback(final AsyncCallback<T> callback) {
        return new AsyncCallback<T>() {
            @Override
            public void onSuccess(T result) {
                if (accessToken != null) authorized = true;
                callback.onSuccess(result);
            }
            
            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }
        };
    }

    private <T extends JavaScriptObject> void get(String url, final AsyncCallback<T> callback) {
        String requestUrl = makeRequestUrl(url);
        GWT.log("[GET]" + requestUrl);
        JsonpRequestBuilder jsonp = new JsonpRequestBuilder();
        jsonp.requestObject(requestUrl, hookCallback(callback));
    }

    private <T extends JavaScriptObject> void post(String url, Value<?> request,
            AsyncCallback<T> callback) {
        String requestUrl = makeRequestUrl(url);
        RequestBuilder builder = new RequestBuilder(RequestBuilder.POST, requestUrl);
        String requestJson = request.toJson();
        final AsyncCallback<T> hookedCallback = hookCallback(callback);
        final StringBuilder log = new StringBuilder();
        log.append("[POST]" + requestUrl + "\n" + requestJson);
        try {
            builder.sendRequest(requestJson, new RequestCallback() {
                @Override
                public void onResponseReceived(Request request, Response response) {
                    T result = JsonUtils.<T>safeEval(response.getText());
                    log.append("\n\n--" + response.getStatusText() + ":" + response.getStatusCode() + "\n" + response.getText());
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

    private String makeRequestUrl(String url) {
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
