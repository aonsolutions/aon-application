package com.esferalia.aon.gwt.office.client;

import java.util.Date;
import java.util.List;

import com.esferalia.aon.gwt.office.client.models.issues.JsIssue;

public interface IssueSelected {

	Integer getId();

	Integer getNumber();

	String getPriority();

	String getType();

	String getTitle();

	String getStateIconStyle();

	String getState();

	String getBody();

	String getCompany();	

	Date getCreateAt();

	Date getUpdatedAt();
	
	DefaultAonUserIssueSelected getUser();
	
	List<DefaultAonTagIssueSelected> getTags();
	
	List<DefaultAonIssueComments> getComments();

	JsIssue getJsIssue();

}
