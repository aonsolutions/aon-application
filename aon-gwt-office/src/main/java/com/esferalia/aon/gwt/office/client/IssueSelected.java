package com.esferalia.aon.gwt.office.client;

import java.util.Date;
import java.util.List;

import com.esferalia.aon.gwt.office.client.models.issues.JsIssue;
import com.esferalia.aon.gwt.office.client.models.issues.JsIssueComment;
import com.esferalia.aon.gwt.office.client.models.issues.JsLabel;

public interface IssueSelected {

	Integer getId();

	Integer getNumber();

	String getPriority();

	String getType();

	String getTitle();

	String getStateIconStyle();

	String getState();

	String getBody();
	
	AonUserIssueSelected getUserAssignee();

	String getCompany();	
	
	int getDuplicates();

	Date getCreateAt();

	Date getUpdatedAt();
	
	AonUserIssueSelected getUser();
	
	AonIssueComments addComment(JsIssueComment comment);
	
	AonIssueComments editComment(JsIssueComment comment);
	
	AonTagIssueSelected editTag(JsLabel label);
	
	void addTag (AonTagIssueSelected tag);
	
	void deleteTag(String name);
	
	void setType(String name);
	
	void setPriority(String priority);
	
	List<AonTagIssueSelected> getTags();
	
	List<AonIssueComments> getComments();

	JsIssue getJsIssue();

}
