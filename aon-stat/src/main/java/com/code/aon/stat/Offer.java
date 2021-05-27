package com.code.aon.stat;

import java.io.Serializable;
import java.util.Date;

import com.code.aon.AonVersion;

public class Offer implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private Integer id;
	private Date issueDate;
	private String reference;
	private String sellerName;
	private String workplaceName;
	private double projectTaxableBase;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Date getIssueDate() {
		return issueDate;
	}
	public void setIssueDate(Date issueDate) {
		this.issueDate = issueDate;
	}
	public String getReference() {
		return reference;
	}
	public void setReference(String reference) {
		this.reference = reference;
	}
	public String getSellerName() {
		return sellerName;
	}
	public void setSellerName(String sellerName) {
		this.sellerName = sellerName;
	}
	public String getWorkplaceName() {
		return workplaceName;
	}
	public void setWorkplaceName(String workplaceName) {
		this.workplaceName = workplaceName;
	}
	public double getProjectTaxableBase() {
		return projectTaxableBase;
	}
	public void setProjectTaxableBase(double projectTaxableBase) {
		this.projectTaxableBase = projectTaxableBase;
	}
	
}
