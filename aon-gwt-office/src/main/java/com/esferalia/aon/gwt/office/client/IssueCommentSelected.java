package com.esferalia.aon.gwt.office.client;

import java.util.Date;

public interface IssueCommentSelected {
	
	Integer getId();
	
	Date getCreatedAt();
	
	Date getUpdatedAt();
	
	String getBody();
	
	DefaultAonUserIssueSelected getUser();
}
