package com.code.aon.stat;

import java.io.Serializable;
import java.util.Date;

import com.code.aon.AonVersion;

public class Delivery implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private Integer id;
	private String type;
	private Date issueDate;
	private String reference;
	private String registryName;
	private double total;
	
	public Integer getId() {
		return id;
	}
	public Delivery setId(Integer id) {
		this.id = id;
		return this;
	}
	public Date getIssueDate() {
		return issueDate;
	}
	public Delivery setIssueDate(Date issueDate) {
		this.issueDate = issueDate;
		return this;
	}
	public String getReference() {
		return reference;
	}
	public Delivery setReference(String reference) {
		this.reference = reference;
		return this;
	}
	public String getRegistryName() {
		return registryName;
	}
	public Delivery setRegistryName(String registryName) {
		this.registryName = registryName;
		return this;
	}
	public double getTotal() {
		return total;
	}
	public Delivery setTotal(double total) {
		this.total = total;
		return this;
	}
	public String getType() {
		return type;
	}
	public Delivery setType(String type) {
		this.type = type;
		return this;
	}
	
}
