package com.esferalia.aon.ui.payroll.controller;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.payroll.enumeration.FileStatus;

public class CertificateBatchParams implements Serializable {

	private static final long serialVersionUID = -3353191952616724056L;

	private String enterprise;
	private String document;
	private String name;
	private String surname;
	private String surname2;
	private Date date;
	private Date startDate;
	private Date endDate;
	private FileStatus[] statusList;
	
	public String getEnterprise() {
		return enterprise;
	}
	public void setEnterprise(String enterprise) {
		this.enterprise = enterprise;
	}
	public String getDocument() {
		return document;
	}
	public void setDocument(String document) {
		this.document = document;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSurname() {
		return surname;
	}
	public void setSurname(String surname) {
		this.surname = surname;
	}
	public String getSurname2() {
		return surname2;
	}
	public void setSurname2(String surname2) {
		this.surname2 = surname2;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public FileStatus[] getStatusList() {
		return statusList;
	}
	public void setStatusList(FileStatus[] statusList) {
		this.statusList = statusList;
	}
	
	
	
}
