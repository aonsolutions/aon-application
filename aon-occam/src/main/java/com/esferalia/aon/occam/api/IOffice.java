package com.esferalia.aon.occam.api;

import java.util.List;

import com.esferalia.aon.occam.api.model.office.Notice;

public interface IOffice {
	
	public List<Notice> getOpenNotices (AONContext ctx) throws IllegalArgumentException;
	public List<Notice> getClosedNotices (AONContext ctx) throws IllegalArgumentException;
	public List<Notice> getAllIssues (AONContext ctx) throws IllegalArgumentException;
	

}
