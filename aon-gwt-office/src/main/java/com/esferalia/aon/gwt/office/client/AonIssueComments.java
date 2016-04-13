package com.esferalia.aon.gwt.office.client;

import java.util.Date;

import com.esferalia.aon.gwt.office.client.models.issues.JsIssueComment;
import com.google.gwt.http.client.URL;
import com.google.gwt.i18n.client.DateTimeFormat;

public class AonIssueComments implements IssueCommentSelected {
	
	protected JsIssueComment issueComment;
	private AonUserIssueSelected user;
	
	private DateTimeFormat timeFormat;
	
	public AonIssueComments(JsIssueComment comment) {
		this.issueComment = comment;
		this.timeFormat = DateTimeFormat.getFormat("yyyy-MM-dd HH:mm:ss.S");		
		this.user = new AonUserIssueSelected(comment.getUser());
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
	public String getCompany() {	
		return URL.decode(issueComment.getCompany());
	}
	
	@Override
	public String getBody() {
		return URL.decode(issueComment.getBody());
	}
	
	@Override
	public AonUserIssueSelected getUser() {		
		return this.user;
	}
}
