package com.esferalia.aon.occam.api;

import java.util.List;

import com.esferalia.aon.occam.api.model.office.Notice;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.office.User;
import com.esferalia.aon.occam.api.model.registry.Registry;

public interface IOffice {
	
	public User getUser (AONContext ctx, Integer id);
	public Notice addNewNotice (AONContext ctx, Notice notice);	
	public Notice editNotice (AONContext ctx, Notice notice);
	public Notice changeNoticeStatus (AONContext ctx, Notice notice);
	public List<Notice> getOpenNotices (AONContext ctx) throws IllegalArgumentException;
	public List<Notice> getClosedNotices (AONContext ctx) throws IllegalArgumentException;
	public List<Notice> getAllIssues (AONContext ctx) throws IllegalArgumentException;
	public boolean deleteNotice (AONContext ctx, Integer id);
	public boolean deleteTag (AONContext ctx, String labelName);
	public Tag addNewTag (AONContext ctx, Tag tag) throws IllegalArgumentException;
	public Tag editTag (AONContext ctx, String labelName, Tag tag);
	public Tag getTag (AONContext ctx, String name) throws IllegalArgumentException;
	public List<Tag> getTags (AONContext ctx) throws IllegalArgumentException;
	public List<Registry> getRegistries (AONContext ctx) throws IllegalArgumentException;

}
