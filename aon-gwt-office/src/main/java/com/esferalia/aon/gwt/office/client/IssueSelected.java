package com.esferalia.aon.gwt.office.client;

import java.util.Date;

import com.esferalia.aon.gwt.office.client.models.issues.JsLabel;
import com.google.gwt.core.client.JsArray;

public interface IssueSelected {
	
	String getTitle();
	
	String getState();
	
	/** UserSelected getUser(); **/
	
	Date getCreateAt();
	
	Date getUpdatedAt();
	
	String getBody();
	
	Integer getComments();
	
	String getAssignee();
	
	Integer getId();
	
	JsArray<JsLabel> getLabels();

}
