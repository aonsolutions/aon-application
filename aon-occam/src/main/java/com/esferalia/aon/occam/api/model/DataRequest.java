package com.esferalia.aon.occam.api.model;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.occam.api.model.type.DataRequestType;

public class DataRequest implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private Integer domain;
	private DataRequestType type;
	private Date date;
	private String blackBox;
	private String md5;

	public Integer getId() {
		return id;
	}
	
	public DataRequest setId(Integer id) {
		this.id = id;
		return this;
	}
	
	public Integer getDomain() {
		return domain;
	}
	
	public DataRequest setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}

	public DataRequestType getType() {
		return type;
	}

	public DataRequest setType(DataRequestType type) {
		this.type = type;
		return this;
	}

	public Date getDate() {
		return date;
	}

	public DataRequest setDate(Date date) {
		this.date = date;
		return this;
	}

	public String getBlackBox() {
		return blackBox;
	}

	public DataRequest setBlackBox(String blackBox) {
		this.blackBox = blackBox;
		return this;
	}

	public String getMd5() {
		return md5;
	}

	public DataRequest setMd5(String md5) {
		this.md5 = md5;
		return this;
	}
}
