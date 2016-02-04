package com.esferalia.aon.gwt.office.client;

import java.util.Date;

import com.esferalia.aon.gwt.office.client.models.issues.JsIssue;
import com.esferalia.aon.gwt.office.client.models.issues.JsIssueComment;
import com.esferalia.aon.gwt.office.client.models.issues.JsLabel;
import com.esferalia.aon.gwt.office.client.models.users.JsUser;
import com.google.gwt.core.client.JsArray;

public interface IssueSelected {
	
	String getTitle();
	
	String getStateIconStyle();
	
	Integer getNumber();
	
	String getState();
	
	String getPriority();
	
	String getType();
	
	JsUser getUser();
	
	Date getCreateAt();
	
	Date getUpdatedAt();
	
	String getBody();
	
	String getCompany();
	
	Integer getComments();
	
	String getAssignee();
	
	Integer getId();
	
	JsArray<JsIssueComment> getIssueComments();
	
	JsArray<JsLabel> getLabels();
	
	JsIssue getJsIssue();
}
