package com.code.aon.groupware;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.aon.common.ITransferObject;
import com.code.aon.config.User;
import com.code.aon.groupware.enumeration.AlarmSource;
import com.code.aon.groupware.enumeration.AlarmStatus;
import com.code.aon.groupware.enumeration.Priority;

@Entity
@Table(name="alarm")
public class Alarm implements ITransferObject {

	private Integer id;
	
	private String description;
	
	private Date alarmDate;
	
	private AlarmStatus status;
	
	private AlarmSource source;
	
	private Integer sourceId;
	
	private User user;
	
	private Priority priority;

	@Id
	@GeneratedValue
	@Column(nullable=false)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(length=65535, nullable=false)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	@Transient
	public String getDescriptionHead(){
		if(getDescription() != null && getDescription().length() >= 64){
			return getDescription().substring(0, 63);
		}
		return getDescription();
	}

	@Column(name="alarm_date", nullable=false)
	public Date getAlarmDate() {
		return alarmDate;
	}
	public void setAlarmDate(Date alarmDate) {
		this.alarmDate = alarmDate;
	}

	public AlarmStatus getStatus() {
		return status;
	}

	public void setStatus(AlarmStatus status) {
		this.status = status;
	}

	public AlarmSource getSource() {
		return source;
	}

	public void setSource(AlarmSource source) {
		this.source = source;
	}

	@Column(name="source_id", nullable=false)
	public Integer getSourceId() {
		return sourceId;
	}

	public void setSourceId(Integer sourceId) {
		this.sourceId = sourceId;
	}

	@ManyToOne
	@JoinColumn(name="user")
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Column(nullable=false)
	public Priority getPriority() {
		return priority;
	}

	public void setPriority(Priority priority) {
		this.priority = priority;
	}
}