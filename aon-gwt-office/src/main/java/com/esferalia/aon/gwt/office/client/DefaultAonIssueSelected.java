package com.esferalia.aon.gwt.office.client;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.gwt.office.client.models.issues.JsIssue;
import com.esferalia.aon.gwt.office.client.models.issues.JsIssueComment;
import com.esferalia.aon.gwt.office.client.models.issues.JsLabel;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.i18n.client.DateTimeFormat;

public abstract class DefaultAonIssueSelected implements IssueSelected {

	protected JsIssue issue;

	private DateTimeFormat timeFormat;

	private DefaultAonUserIssueSelected user;
	private List<DefaultAonTagIssueSelected> tags;
	private List<DefaultAonIssueComments> comments;

	public DefaultAonIssueSelected(JsIssue issue) {
		this.issue = issue;
		this.timeFormat = DateTimeFormat.getFormat("yyyy-MM-dd HH:mm:ss.S");

		this.user = new DefaultAonUserIssueSelected(issue.getUser());
		
		this.tags = new LinkedList<DefaultAonTagIssueSelected>();
		this.comments = new LinkedList<DefaultAonIssueComments>();
		
		initTagList(issue.getLabels());
		initCommentList(issue.getIssueComments());

	}

	private void initTagList(JsArray<JsLabel> label) {

		for (int x = 0; x < label.length(); x++) {
			DefaultAonTagIssueSelected defaultTag = new DefaultAonTagIssueSelected(
					label.get(x));
			tags.add(defaultTag);
		}
	}
	
	private void initCommentList(JsArray<JsIssueComment> comments) {
		
		for ( int x = 0; x < comments.length(); x++) {
			DefaultAonIssueComments defaultComment = new DefaultAonIssueComments(comments.get(x));
			this.comments.add(defaultComment);
		}
	}

	@Override
	public Integer getId() {
		return issue.getId();
	}

	@Override
	public Integer getNumber() {
		return issue.getNumber();
	}

	@Override
	public String getState() {
		return issue.getState();
	}

	@Override
	public String getStateIconStyle() {
		return "";
	}
	
	@Override
	public String getCompany() {		
		return issue.getCompany();
	}

	@Override
	public String getPriority() {
		return issue.getPriority();
	}

	@Override
	public String getType() {
		return issue.getType();
	}

	@Override
	public String getTitle() {
		return issue.getTitle();
	}

	@Override
	public String getBody() {
		return issue.getBody();
	}

	@Override
	public Date getCreateAt() {
		Date date = timeFormat.parse(issue.getCreatedAt());
		String dateAsString = DateTimeFormat.getFormat("dd-MM-yyyy HH:mm")
				.format(date);
		return DateTimeFormat.getFormat("dd-MM-yyyy HH:mm").parse(dateAsString);
	}

	@Override
	public Date getUpdatedAt() {
		Date date = timeFormat.parse(issue.getCreatedAt());
		String dateAsString = DateTimeFormat.getFormat("dd-MM-yyyy HH:mm")
				.format(date);
		return DateTimeFormat.getFormat("dd-MM-yyyy HH:mm").parse(dateAsString);
	}

	@Override
	public DefaultAonUserIssueSelected getUser() {
		return this.user;
	}
	
	@Override
	public List<DefaultAonTagIssueSelected> getTags() {		
		return tags;
	}
	
	@Override
	public DefaultAonIssueComments addComment(JsIssueComment comment) {
		DefaultAonIssueComments newComment = new DefaultAonIssueComments(comment);
		this.comments.add(newComment);
		return newComment;
		
	}	
	
	@Override
	public List<DefaultAonIssueComments> getComments() {
		return comments;
	}
	
	public JsIssue getJsIssue() {
		return this.issue;
	}
}
