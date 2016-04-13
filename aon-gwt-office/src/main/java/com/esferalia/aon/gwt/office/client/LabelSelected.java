package com.esferalia.aon.gwt.office.client;

import java.util.Date;

public interface LabelSelected {
	
	Integer getId();
	
	byte getType();
	
	String getName();
	
	String getColor();
	
	Integer getDomain();
	
	Date getCreateAt();
	
	Date getDeletedAt();
	
	AonUserIssueSelected getUser();
	
	boolean endDateIsNull();
	
	boolean isOfficeStatus();
	
	boolean isOfficeType();
	
	boolean isOfficePriority();
	
	boolean isOfficeNotice();
}
