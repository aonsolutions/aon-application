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
import com.esferalia.aon.gwt.office.client.values.issues.IssueValue;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface GitHubService {
	
	abstract void setGitHubUrl(String url);
	
	abstract void setAccessToken(String accessToken);
	
	abstract boolean isAuthorized();
	
	void getUser(String login, final AsyncCallback<AJSON<JsUser>> callback);

	void getUser(final AsyncCallback<AJSON<JsUser>> callback);

	void getRepos(AsyncCallback<JSON<JsRepo>> callback);

	void getRepos(String user, AsyncCallback<JSON<JsRepo>> callback);

	void getRepo(String login, String name,
			AsyncCallback<AJSON<JsRepo>> callback);

	void saveRepo(JsRepo r, RepoValue prop, AsyncCallback<JsRepo> callback);

	void getIssues(String user, String r, AsyncCallback<JSON<JsIssue>> callback);

	void getIssues(JsRepo r, AsyncCallback<JSON<JsIssue>> callback);

	void createIssue(JsRepo r, IssueValue prop,
			final AsyncCallback<JsIssue> callback);

	void editIssue(JsRepo r, JsIssue issue, IssueValue prop,
			final AsyncCallback<JsIssue> callback);

	void getIssueComments(JsRepo r, JsIssue issue,
			AsyncCallback<JSON<JsIssueComment>> callback);

	void createIssueComment(JsRepo r, JsIssue issue, IssueCommentValue prop,
			final AsyncCallback<JsIssueComment> callback);

	void getLabels(JsRepo repo, AsyncCallback<JSON<JsLabel>> callback);

	void createLabel(JsRepo repo, LabelValue prop,
			final AsyncCallback<JsLabel> callback);

	void saveLabel(JsRepo repo, String name, LabelValue prop,
			final AsyncCallback<JsLabel> callback);

	void saveLabel(JsRepo repo, JsLabel label, LabelValue prop,
			final AsyncCallback<JsLabel> callback);
}
