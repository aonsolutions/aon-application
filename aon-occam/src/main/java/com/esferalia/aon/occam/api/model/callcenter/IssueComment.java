package com.esferalia.aon.occam.api.model.callcenter;

import java.io.Serializable;
import java.sql.Timestamp;

public class IssueComment implements Serializable{


	/**
	 * 
	 */
	private static final long serialVersionUID = -638896778770891272L;
	
	
	
	private Integer id;
	private Integer domain;
	private String subject;
	private Timestamp date;
	private Integer ownerId;
	private String note;
	
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getDomain() {
		return domain;
	}
	public void setDomain(Integer domain) {
		this.domain = domain;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public Timestamp getDate() {
		return date;
	}
	public void setDate(Timestamp date) {
		this.date = date;
	}
	public Integer getOwnerId() {
		return ownerId;
	}
	public void setOwnerId(Integer ownerId) {
		this.ownerId = ownerId;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}

	
			
	
}
