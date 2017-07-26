package com.esferalia.aon.occam.api.model;

import java.io.Serializable;
import java.util.Date;

public class DataResponseDetail implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private Integer domain;
	private Integer dataResponse;
	private String dataVariable;
	private String dataValue;
	private String creationUser;
	private Date creationDate;
	private String modificationUser;
	private Date modificationDate;
	
	public Integer getId() {
		return id;
	}
	public DataResponseDetail setId(Integer id) {
		this.id = id;
		return this;
	}
	public Integer getDomain() {
		return domain;
	}
	public DataResponseDetail setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	public Integer getDataResponse() {
		return dataResponse;
	}
	public DataResponseDetail setDataResponse(Integer dataResponse) {
		this.dataResponse = dataResponse;
		return this;
	}
	public String getDataVariable() {
		return dataVariable;
	}
	public DataResponseDetail setDataVariable(String dataVariable) {
		this.dataVariable = dataVariable;
		return this;
	}
	public String getDataValue() {
		return dataValue;
	}
	public DataResponseDetail setDataValue(String dataValue) {
		this.dataValue = dataValue;
		return this;
	}
	public Date getCreationDate() {
		return creationDate;
	}
	public DataResponseDetail setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
		return this;
	}
	public String getCreationUser() {
		return creationUser;
	}
	public DataResponseDetail setCreationUser(String creationUser) {
		this.creationUser = creationUser;
		return this;
	}
	public Date getModificationDate() {
		return modificationDate;
	}
	public DataResponseDetail setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
		return this;
	}
	public String getModificationUser() {
		return modificationUser;
	}
	public DataResponseDetail setModificationUser(String modificationUser) {
		this.modificationUser = modificationUser;
		return this;
	}
	
}
