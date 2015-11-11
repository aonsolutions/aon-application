package com.esferalia.aon.occam.api.model.office;

import java.io.Serializable;
import java.sql.Timestamp;

public class NoticeComment implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private Integer domain;
	private Timestamp date;
	private String body;
	
	public NoticeComment() {

	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public void setDomain(Integer domain) {
		this.domain = domain;
	}
	
	public void setDate(Timestamp date) {
		this.date = date;
	}
	
	public void setBody(String body) {
		this.body = body;
	}
	
	public Integer getId() {
		return id;
	}
	
	public Integer getDomain() {
		return domain;
	}
	
	public Timestamp getDate() {
		return date;
	}
	
	public String getBody() {
		return body;
	}

}
