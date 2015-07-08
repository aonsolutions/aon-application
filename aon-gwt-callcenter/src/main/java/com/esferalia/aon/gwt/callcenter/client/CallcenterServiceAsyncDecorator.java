package com.esferalia.aon.gwt.callcenter.client;

import java.util.ArrayList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.occam.api.model.callcenter.Issue;
import com.esferalia.aon.occam.api.model.callcenter.IssueComment;
import com.google.gwt.user.client.rpc.AsyncCallback;


public class CallcenterServiceAsyncDecorator implements CallcenterServiceAsync {

	private CallcenterServiceAsync serviceAsync;

	public CallcenterServiceAsyncDecorator(CallcenterServiceAsync serviceAsync) {
		this.serviceAsync = serviceAsync;
	}

	// NOTICE

	@Override
	public void getOpenIssues(String domainName, int domain, String subject,
			AsyncCallback<ArrayList<Issue>> callback) {
		AON.start();
		serviceAsync.getOpenIssues(domainName, domain, subject,  
				new AsyncCallbackWrapper<ArrayList<Issue>>(callback));
	}

	@Override
	public void getClosedIssues(String domainName, int domain, String subject,
			AsyncCallback<ArrayList<Issue>> callback) {
		AON.start();
		serviceAsync.getClosedIssues(domainName, domain, subject,  
				new AsyncCallbackWrapper<ArrayList<Issue>>(callback));
	}
	
	@Override
	public void getIssueComments(String domainName, int domain, int issue,
			AsyncCallback<ArrayList<IssueComment>> callback){
		AON.start();
		serviceAsync.getIssueComments(domainName, domain, issue,   
				new AsyncCallbackWrapper<ArrayList<IssueComment>>(callback));
	}


}
