package es.translogia.tedi.ewok;

import java.io.Serializable;
import java.util.Date;

public class TediComments implements Serializable {
	
	private static final long serialVersionUID = 6865210693414641809L;
	
	private Date date;
	private String comment;
	private String user;
	
	public Date getDate() {
		return date;
	}
	public TediComments setDate(Date date) {
		this.date = date;
		return this;
	}
	
	public String getComment() {
		return comment;
	}
	public TediComments setComment(String comment) {
		this.comment = comment;
		return this;
	}
	
	public String getUser() {
		return user;
	}
	public TediComments setUser(String user) {
		this.user = user;
		return this;
	}


}
