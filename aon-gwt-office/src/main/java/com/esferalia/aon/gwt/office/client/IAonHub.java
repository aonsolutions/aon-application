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
import com.esferalia.aon.gwt.office.client.values.issues.IssueValue;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface IAonHub {

	void getUser(String login, final AsyncCallback<AJSON<JsUser>> callback);

	void createRepository(RepoValue prop, AsyncCallback<JsRepo> callback);

	void getRepoOrganization(String organization,
			AsyncCallback<JSON<JsRepo>> callback);

	void getRepos(String user, AsyncCallback<JSON<JsRepo>> callback);

	void getRepo(String login, String name,
			AsyncCallback<AJSON<JsRepo>> callback);

	void saveRepo(RepoValue prop, AsyncCallback<JsRepo> callback);

	void deleteRepository(AsyncCallback<JsRepo> callback);

	void getOpenIssues(String user, String repo,
			AsyncCallback<JSON<JsIssue>> callback);

	void getClosedIssues(String user, String repo,
			AsyncCallback<JSON<JsIssue>> callback);

	void getAllIssues(String user, String repo,
			AsyncCallback<JSON<JsIssue>> callback);

	@Deprecated
	void getIssues(String user, String r,
			AsyncCallback<JSON<JsIssue>> callback);

	@Deprecated
	void getIssues(JsRepo r, AsyncCallback<JSON<JsIssue>> callback);

	void createIssue(IssueValue prop,
			final AsyncCallback<JsIssue> callback);

	void editIssue(JsIssue issue, IssueValue prop,
			final AsyncCallback<JsIssue> callback);

	void deleteIssue(String name,
			final AsyncCallback<JsIssue> callback);

	void getIssueComments(JsIssue issue,
			AsyncCallback<JSON<JsIssueComment>> callback);

	void createIssueComment(JsIssue issue, IssueCommentValue prop,
			final AsyncCallback<JsIssueComment> callback);

	void editIssueComment(String user, String repo, Integer id,
			IssueCommentValue prop,
			final AsyncCallback<JsIssueComment> callback);
	
	void deleteIssueComment(Integer id, final AsyncCallback<JsIssue> callback);

	void getLabels(AsyncCallback<JSON<JsLabel>> callback);

	void createLabel(LabelValue prop,
			final AsyncCallback<JsLabel> callback);

	void saveLabel(String name, LabelValue prop,
			final AsyncCallback<JsLabel> callback);

	void saveLabel(JsLabel label, LabelValue prop,
			final AsyncCallback<JsLabel> callback);
	
	void deleteLabel(String labelName, final AsyncCallback<JsLabel> callback);
}
