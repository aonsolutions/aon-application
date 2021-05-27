package com.code.aon.stat.tas;


import java.util.Date;

public class TasStatParams {

	private Date fromDate;
	private Date toDate;
	private Integer tasItem;
	private Integer target;
	private Integer model;
	private String publicCode;
	private String privateCode;
	private String description;
	private String addInfo;
	private String domainName;
	
	public TasStatParams(String domainName) {
		this.domainName = domainName; 	
	}

	public String getDomainName() {
		return domainName;
	}
	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}


	public Date getFromDate() {
		return fromDate;
	}
	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}
	
	public Date getToDate() {
		return toDate;
	}
	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}
	
	public Integer getTarget() {
		return target;
	}
	public void setTarget(Integer target) {
		this.target = target;
	}
	
	public Integer getTasItem() {
		return tasItem;
	}
	public void setTasItem(Integer tasItem) {
		this.tasItem = tasItem;
	}
	public Integer getModel() {
		return model;
	}
	public void setModel(Integer model) {
		this.model = model;
	}
	public String getPublicCode() {
		return publicCode;
	}
	public void setPublicCode(String publicCode) {
		this.publicCode = publicCode;
	}
	public String getPrivateCode() {
		return privateCode;
	}
	public void setPrivateCode(String privateCode) {
		this.privateCode = privateCode;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getAddInfo() {
		return addInfo;
	}
	public void setAddInfo(String addInfo) {
		this.addInfo = addInfo;
	}
}
