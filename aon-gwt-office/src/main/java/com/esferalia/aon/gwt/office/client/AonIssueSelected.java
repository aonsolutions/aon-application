package com.esferalia.aon.gwt.office.client;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.gwt.office.client.models.issues.JsIssue;
import com.esferalia.aon.gwt.office.client.models.issues.JsIssueComment;
import com.esferalia.aon.gwt.office.client.models.issues.JsLabel;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.http.client.URL;
import com.google.gwt.i18n.client.DateTimeFormat;

public abstract class AonIssueSelected implements IssueSelected {

	protected JsIssue issue;

	private DateTimeFormat timeFormat;

	private AonUserIssueSelected user;
	private List<AonTagIssueSelected> tags;
	private List<AonIssueComments> comments;
	
	private String title;
//	private String body;
	private String type;
	private String priority;
	
	public AonIssueSelected(JsIssue issue) {
		this.issue = issue;
		this.timeFormat = DateTimeFormat.getFormat("yyyy-MM-dd HH:mm:ss.S");

		this.user = new AonUserIssueSelected(issue.getUser());
		
		this.tags = new LinkedList<AonTagIssueSelected>();
		this.comments = new LinkedList<AonIssueComments>();
		
		this.title = URL.decode(issue.getTitle());
//		this.body = URL.decode(issue.getBody());
		this.type = URL.decode(issue.getType());
		this.priority = URL.decode(issue.getPriority());
		
		initTagList(issue.getLabels());
		initCommentList(issue.getIssueComments());

	}

	private void initTagList(JsArray<JsLabel> label) {

		for (int x = 0; x < label.length(); x++) {
			AonTagIssueSelected defaultTag = new AonTagIssueSelected(
					label.get(x));
			tags.add(defaultTag);
		}
	}
	
	private void initCommentList(JsArray<JsIssueComment> comments) {
		
		for ( int x = 0; x < comments.length(); x++) {
			AonIssueComments defaultComment = new AonIssueComments(comments.get(x));
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
		return URL.decode(issue.getCompany());
	}	
	
	@Override
	public void setPriority(String priority) {
		this.priority = priority;
	}

	@Override
	public String getPriority() {
		return this.priority;
	}
	
	@Override
	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String getType() {
		return this.type;
	}

	@Override
	public String getTitle() {
		return this.title;
	}

	@Override
	public String getBody() {
//		return this.body;
		return "";
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
	public AonUserIssueSelected getUser() {
		return this.user;
	}
	
	@Override
	public List<AonTagIssueSelected> getTags() {		
		return tags;
	}
	
	@Override
	public void addTag(AonTagIssueSelected tag) {
		getTags().add(tag);
	}
	
	@Override
	public void deleteTag(String name) {
		
		for (AonTagIssueSelected tag : getTags()) {
			if (tag.getName().compareTo(name) == 0) {
				getTags().remove(tag);
				break;
			}
		}		
	}
	
	@Override
	public AonIssueComments addComment(JsIssueComment comment) {
		AonIssueComments newComment = new AonIssueComments(comment);
		this.comments.add(newComment);
		return newComment;
		
	}	
	
	@Override
	public List<AonIssueComments> getComments() {
		return comments;
	}
	
	public JsIssue getJsIssue() {
		return this.issue;
	}
}
