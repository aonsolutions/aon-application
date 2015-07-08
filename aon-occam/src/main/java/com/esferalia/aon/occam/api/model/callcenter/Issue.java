package com.esferalia.aon.occam.api.model.callcenter;

import java.io.Serializable;
import java.sql.Timestamp;

public class Issue implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7122883148262657305L;
	
	
	
	private Integer id;
	private Integer domain;
	private Timestamp date;
	private Integer senderId;
	private Integer workGroupId;
	private Integer recipientId;
	private String source;
	private String company;
	private String phone;
	private String subject;
	private Byte status;
	private Byte type;
	private Byte priority;
	
	
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
	public Timestamp getDate() {
		return date;
	}
	public void setDate(Timestamp date) {
		this.date = date;
	}
	public Integer getSenderId() {
		return senderId;
	}
	public void setSenderId(Integer senderId) {
		this.senderId = senderId;
	}
	public Integer getWorkGroupId() {
		return workGroupId;
	}
	public void setWorkGroupId(Integer workGroupId) {
		this.workGroupId = workGroupId;
	}
	public Integer getRecipientId() {
		return recipientId;
	}
	public void setRecipientId(Integer recipientId) {
		this.recipientId = recipientId;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public Byte getStatus() {
		return status;
	}
	public void setStatus(Byte status) {
		this.status = status;
	}
	public Byte getType() {
		return type;
	}
	public void setType(Byte type) {
		this.type = type;
	}
	public Byte getPriority() {
		return priority;
	}
	public void setPriority(Byte priority) {
		this.priority = priority;
	}

		
	
}
