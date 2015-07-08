package com.esferalia.aon.gwt.callcenter.client;

import java.util.ArrayList;

import com.esferalia.aon.gwt.common.shared.AonSQLException;
import com.esferalia.aon.occam.api.model.callcenter.Issue;
import com.esferalia.aon.occam.api.model.callcenter.IssueComment;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("Callcenter")
public interface CallcenterService extends RemoteService {

	// NOTICES
	
	ArrayList<Issue> getOpenIssues(String domainName,int domain,String subject) throws AonSQLException;

	ArrayList<Issue> getClosedIssues(String domainName,int domain,String subject) throws AonSQLException;
	
	ArrayList<IssueComment> getIssueComments(String domainName, int domain, int issue) throws AonSQLException;

}
