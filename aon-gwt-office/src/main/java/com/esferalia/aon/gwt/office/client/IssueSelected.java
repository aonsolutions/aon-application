package com.esferalia.aon.gwt.office.client;

import java.util.Date;

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
	
	/** List<LabelSelected> getLabels(); **/

}
