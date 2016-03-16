package com.esferalia.aon.occam.api.model.office;

import java.io.Serializable;
import java.util.Date;

public class NoticeFilter implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private boolean opened;
	private boolean closed;
	private boolean all;
	
	private Date since;
	private String sender;
	private String[] tags;
	private String text;
	
	private int offset;
	
	public void setOpened(boolean opened) {
		this.opened = opened;
	}
	
	public boolean isOpened() {
		return this.opened;
	}
	
	public void setClosed(boolean closed) {
		this.closed = closed;
	}
	
	public boolean isClosed() {
		return this.closed;
	}
	
	public void setAll(boolean all) {
		this.all = all;
	}
	
	public boolean isAll() {
		return this.all;
	}
	
	public void setSince(Date since) {
		this.since = since;
	}
	
	public Date getSince() {
		return since;
	}
	
	public void setSender(String sender) {
		this.sender = sender;
	}
	
	public String getSender() {
		return sender;
	}
	
	public void setTags(String[] tags) {
		this.tags = tags;
	}
	
	public String[] getTags() {
		return tags;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	public String getText() {
		return text;
	}
	
	public void setOffset(int offset) {
		this.offset = offset;
	}
	
	public int getOffset() {
		return offset;
	}
}