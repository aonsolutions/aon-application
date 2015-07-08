package com.esferalia.aon.occam.api;

import java.util.ArrayList;

import com.esferalia.aon.occam.api.model.callcenter.Issue;
import com.esferalia.aon.occam.api.model.callcenter.IssueComment;

public interface IGroupware {

	public ArrayList<Issue> getOpenIssues(AONContext ctx, int domain, String subject);
	public ArrayList<Issue> getClosedIssues(AONContext ctx, int domain, String subject);
	public ArrayList<IssueComment> getIssueComments(AONContext ctx, int issue);
	
}
