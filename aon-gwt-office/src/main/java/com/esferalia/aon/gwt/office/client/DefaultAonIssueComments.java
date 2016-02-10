package com.esferalia.aon.gwt.office.client;

import java.util.Date;

import com.esferalia.aon.gwt.office.client.models.issues.JsIssueComment;
import com.google.gwt.http.client.URL;
import com.google.gwt.i18n.client.DateTimeFormat;

public class DefaultAonIssueComments implements IssueCommentSelected {
	
	protected JsIssueComment issueComment;
	private DefaultAonUserIssueSelected user;
	
	private DateTimeFormat timeFormat;
	
	private String body;
	
	public DefaultAonIssueComments(JsIssueComment comment) {
		this.issueComment = comment;
		this.timeFormat = DateTimeFormat.getFormat("yyyy-MM-dd HH:mm:ss.S");
		this.body = URL.decode(comment.getBody());
		this.user = new DefaultAonUserIssueSelected(comment.getUser());
	}
	
	@Override
	public Integer getId() {	
		return issueComment.getId();
	}
	
	@Override
	public Date getCreatedAt() {
		Date date = timeFormat.parse(issueComment.getCreateAt());
		String dateAsString = DateTimeFormat.getFormat("dd-MM-yyyy HH:mm").format(date);
		return DateTimeFormat.getFormat("dd-MM-yyyy HH:mm").parse(dateAsString);
	}
	
	@Override
	public Date getUpdatedAt() {
		Date date = timeFormat.parse(issueComment.getCreateAt());
		String dateAsString = DateTimeFormat.getFormat("dd-MM-yyyy HH:mm").format(date);
		return DateTimeFormat.getFormat("dd-MM-yyyy HH:mm").parse(dateAsString);
	}
	
	@Override
	public String getBody() {
		return this.body;
//		return issueComment.getBody();
	}
	
	@Override
	public DefaultAonUserIssueSelected getUser() {		
		return this.user;
	}
}
