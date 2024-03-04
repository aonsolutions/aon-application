package com.esferalia.aon.occam.api.model;

import java.io.Serializable;

public class EnterpriseCCC implements Serializable {
	
	private static final long serialVersionUID = -6673378118872331822L;
	
	private Integer id;
	private Integer domain;
	private String ccc;
	private Byte type;
	private Integer enterpriseActivity;
	private Integer geozone;
	private String geozoneCode;
	private String geozoneDescription;
	
	private boolean useByContracts = false;
	private boolean useByCra = false;
	private boolean isDeleted = false;
	
	private String enterpriseName;
	
	public Integer getId() {
		return id;
	}
	public EnterpriseCCC setId(Integer id) {
		this.id = id;
		return this;
	}
	public Integer getDomain() {
		return domain;
	}
	public EnterpriseCCC setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	public String getCcc() {
		return ccc;
	}
	public EnterpriseCCC setCcc(String ccc) {
		this.ccc = ccc;
		return this;
	}
	public Byte getType() {
		return type;
	}
	public EnterpriseCCC setType(Byte type) {
		this.type = type;
		return this;
	}
	public Integer getEnterpriseActivity() {
		return enterpriseActivity;
	}
	public EnterpriseCCC setEnterpriseActivity(Integer enterpriseActivity) {
		this.enterpriseActivity = enterpriseActivity;
		return this;
	}
	public Integer getGeozone() {
		return geozone;
	}
	public EnterpriseCCC setGeozone(Integer geozone) {
		this.geozone = geozone;
		return this;
	}
	public String getGeozoneCode() {
		return geozoneCode;
	}
	public EnterpriseCCC setGeozoneCode(String geozoneCode) {
		this.geozoneCode = geozoneCode;
		return this;
	}
	public String getGeozoneDescription() {
		return geozoneDescription;
	}
	public EnterpriseCCC setGeozoneDescription(String geozoneDescription) {
		this.geozoneDescription = geozoneDescription;
		return this;
	}
	public boolean isDeleted() {
		return isDeleted;
	}
	public EnterpriseCCC setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
		return this;
	}
	public boolean isUseByContracts() {
		return useByContracts;
	}
	public EnterpriseCCC setUseByContracts(boolean useByContracts) {
		this.useByContracts = useByContracts;
		return this;
	}
	public boolean isUseByCra() {
		return useByCra;
	}
	public EnterpriseCCC setUseByCra(boolean useByCra) {
		this.useByCra = useByCra;
		return this;
	}
	
	public String getEnterpriseName() {
	    return enterpriseName;
	}
	
	public void setEnterpriseName(String enterpriseName) {
	    this.enterpriseName = enterpriseName;
	}
	
}
