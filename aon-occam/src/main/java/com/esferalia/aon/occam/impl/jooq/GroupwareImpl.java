package com.esferalia.aon.occam.impl.jooq;

import java.util.ArrayList;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.IGroupware;
import com.esferalia.aon.occam.api.model.callcenter.Issue;
import com.esferalia.aon.occam.api.model.callcenter.IssueComment;
import com.esferalia.aon.occam.impl.jooq.dao.GroupwareDAO;

public class GroupwareImpl implements IGroupware {

	
	@Override
	public ArrayList<Issue> getOpenIssues(AONContext ctx, int domain, String subject) {
		return GroupwareDAO.getOpenIssues(ctx, domain, subject);
	}

	@Override
	public ArrayList<Issue> getClosedIssues(AONContext ctx, int domain, String subject) {
		return GroupwareDAO.getClosedIssues(ctx, domain, subject);
	}

	@Override
	public ArrayList<IssueComment> getIssueComments(AONContext ctx, int issue) {
		return GroupwareDAO.getIssueComments(ctx, issue);
	}

}
