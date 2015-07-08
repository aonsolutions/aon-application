package com.esferalia.aon.gwt.callcenter.client;

import java.util.ArrayList;

import com.esferalia.aon.occam.api.model.callcenter.Issue;
import com.esferalia.aon.occam.api.model.callcenter.IssueComment;
import com.google.gwt.user.client.rpc.AsyncCallback;



public interface CallcenterServiceAsync {

	// NOTICE
	
	void getOpenIssues(String domainName, int domain, String subject
			,AsyncCallback<ArrayList<Issue>> callback
			);
	void getClosedIssues(String domainName, int domain, String subject
			,AsyncCallback<ArrayList<Issue>> callback
			);
	void getIssueComments(String domainName, int domain, int issue
			,AsyncCallback<ArrayList<IssueComment>> callback
			);
}
