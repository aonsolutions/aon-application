package com.esferalia.aon.occam.api.model.office;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class NoticeFilter implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static SimpleDateFormat sdf = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss'Z'");
	
	private boolean opened;
	private boolean closed;
	private boolean all;
	private boolean duplicated;
	private boolean faq;
	
	private Date since;
//	private String sinceAsString;
	private String company;
	private String[] tags;
	private String subject;
	private String user;
	
	private int offset;
	
	/**
	 * 
	 * @param state open || closed || all || faq
	 */
	
	public void setState(String state) {
		
		switch (state) {
		case "open":
			setOpened(true);
			break;
		case "closed":
			setClosed(true);
			break;
		case "all":
			setAll(true);
			break;
		case "faq":
			setFaq(true);
			break;
		default:
			setAll(true);
			break;
		}
	}
	
	private void setOpened(boolean opened) {
		this.opened = opened;
	}
	
	public boolean isOpened() {
		return this.opened;
	}
	
	private void setClosed(boolean closed) {
		this.closed = closed;
	}
	
	public boolean isClosed() {
		return this.closed;
	}
	
	private void setAll(boolean all) {
		this.all = all;
	}
	
	private void setFaq(boolean faq) {
		this.faq = faq;
	}
	
	public boolean isAll() {
		return this.all;
	}
	
	public boolean isFaq() {
		return this.faq;
	}
	
	public void setDuplicated(boolean duplicated) {
		this.duplicated = duplicated;
	}
	
	public boolean isDuplicated() {
		return this.duplicated;
	}
	
	public void setSince(Date since) {
		this.since = since;
	}
	
	public Date getSince() {
		return since;
	}
	
	public void setSinceAsString(String since) {
		
		Date sinceAux = null;
		
		try {
			sinceAux = sdf.parse(since);
		} catch (Exception ex) {
			sinceAux = new Date();
		}
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(sinceAux);	
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		
		this.since = cal.getTime();
	}
	
	public void setCompany(String sender) {
		this.company = sender;
	}
	
	public String getComany() {
		return company;
	}
	
	public void setTags(String[] tags) {
		this.tags = tags;
	}
	
	public String[] getTags() {
		if (tags == null)
			return new String[0];
		return tags;
	}
	
	public void setText(String text) {
		this.subject = text;
	}
	
	public String getText() {
		return subject;
	}
	
	public void setOffset(int offset) {
		this.offset = offset;
	}
	
	public int getOffset() {
		return offset;
	}
	
	public void setUser(String user) {
		this.user = user;
	}
	
	public String getUser() {
		if (user == null)
			return "";
		return user;
	}
}