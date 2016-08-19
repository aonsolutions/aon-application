package com.esferalia.aon.occam.api.model;

import java.io.Serializable;
import java.util.Date;

public class Alarm implements Serializable, HasId, HasDomain {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer id;
	private Integer domain;
	private String description;
	private Date alarmDate;
	private Byte status;
	private Byte source;
	private Integer sourceId;
	private Integer userId;
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
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Date getAlarmDate() {
		return alarmDate;
	}
	public void setAlarmDate(Date alarmDate) {
		this.alarmDate = alarmDate;
	}
	public Byte getStatus() {
		return status;
	}
	public void setStatus(Byte status) {
		this.status = status;
	}
	public Byte getSource() {
		return source;
	}
	public void setSource(Byte source) {
		this.source = source;
	}
	public Integer getSourceId() {
		return sourceId;
	}
	public void setSourceId(Integer sourceId) {
		this.sourceId = sourceId;
	}
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public Byte getPriority() {
		return priority;
	}
	public void setPriority(Byte priority) {
		this.priority = priority;
	}


	
}
