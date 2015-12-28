package com.esferalia.aon.occam.api;

import java.util.List;

import com.esferalia.aon.occam.api.model.office.Notice;
import com.esferalia.aon.occam.api.model.office.Tag;

public interface IOffice {
	
	public List<Notice> getOpenNotices (AONContext ctx) throws IllegalArgumentException;
	public List<Notice> getClosedNotices (AONContext ctx) throws IllegalArgumentException;
	public List<Notice> getAllIssues (AONContext ctx) throws IllegalArgumentException;
	public Tag addNewTag (AONContext ctx, Tag tag) throws IllegalArgumentException;
	public List<Tag> getTags (AONContext ctx) throws IllegalArgumentException;

}
