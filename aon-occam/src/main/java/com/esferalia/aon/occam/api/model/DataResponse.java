package com.esferalia.aon.occam.api.model;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.occam.api.model.type.DataResponseSource;

public class DataResponse implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private Integer domain;
	private String code;
	private Date responseDate;
	private DataResponseSource source;
	private Integer sourceId;
	private String creationUser;
	private Date creationDate;
	private String modificationUser;
	private Date modificationDate;
	
	public Integer getId() {
		return id;
	}
	public DataResponse setId(Integer id) {
		this.id = id;
		return this;
	}
	public Integer getDomain() {
		return domain;
	}
	public DataResponse setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	public String getCode() {
		return code;
	}
	public DataResponse setCode(String code) {
		this.code = code;
		return this;
	}
	public Date getResponseDate() {
		return responseDate;
	}
	public DataResponse setResponseDate(Date responseDate) {
		this.responseDate = responseDate;
		return this;
	}
	public Date getCreationDate() {
		return creationDate;
	}
	public DataResponse setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
		return this;
	}
	public String getCreationUser() {
		return creationUser;
	}
	public DataResponse setCreationUser(String creationUser) {
		this.creationUser = creationUser;
		return this;
	}
	public Date getModificationDate() {
		return modificationDate;
	}
	public DataResponse setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
		return this;
	}
	public String getModificationUser() {
		return modificationUser;
	}
	public DataResponse setModificationUser(String modificationUser) {
		this.modificationUser = modificationUser;
		return this;
	}
	public DataResponseSource getSource() {
		return source;
	}
	public DataResponse setSource(DataResponseSource source) {
		this.source = source;
		return this;
	}
	public Integer getSourceId() {
		return sourceId;
	}
	public DataResponse setSourceId(Integer sourceId) {
		this.sourceId = sourceId;
		return this;
	}
	
	
}
