package com.esferalia.aon.gwt.callcenter.server;

import java.util.ArrayList;

import javax.servlet.annotation.WebServlet;

import com.esferalia.aon.gwt.callcenter.client.CallcenterService;
import com.esferalia.aon.gwt.common.server.AonRemoteServiceServlet;
import com.esferalia.aon.gwt.common.shared.AonSQLException;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.callcenter.Issue;
import com.esferalia.aon.occam.api.model.callcenter.IssueComment;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
@WebServlet(name = "Callcenter Servlet", urlPatterns = { "/aon_gwt_callcenter/Callcenter" })
public class CallcenterServiceImpl extends AonRemoteServiceServlet implements CallcenterService {

	
	@Override
	public ArrayList<Issue> getOpenIssues(String domainName,
			int domain, String subject) throws AonSQLException {
		return AON.getOpenIssues(domainName, domain, subject);
	}

	@Override
	public ArrayList<Issue> getClosedIssues(String domainName,
			int domain, String subject) throws AonSQLException {
		return AON.getClosedIssues(domainName, domain, subject);
	}
	
	@Override
	public ArrayList<IssueComment> getIssueComments(String domainName,
			int domain, int issue)
			throws AonSQLException {
		return AON.getIssueComments(domainName, domain, issue);
	}


}
