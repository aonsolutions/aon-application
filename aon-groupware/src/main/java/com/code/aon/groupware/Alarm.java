package com.code.aon.groupware;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Type;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.config.User;
import com.code.aon.groupware.enumeration.AlarmSource;
import com.code.aon.groupware.enumeration.AlarmStatus;
import com.code.aon.groupware.enumeration.Priority;

@Entity
@Table(name="alarm")
public class Alarm implements ITransferObject {

	private static final long serialVersionUID = 5331239358786123832L;

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

	@Column(nullable=false)
	@Lob
	@Type(type="stringClob")	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

	@Column(nullable=false)
	public AlarmSource getSource() {
		return source;
	}

	public void setSource(AlarmSource source) {
		this.source = source;
	}

	@Column(name="source_id")
	public Integer getSourceId() {
		return sourceId;
	}

	public void setSourceId(Integer sourceId) {
		this.sourceId = sourceId;
	}

	@ManyToOne
	@JoinColumn(name="user_id")
	@ForeignKey(name = "FK_ALARM_USER_ID")
	@Index(name = "IDX_ALARM_USER_ID")
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Column(nullable=false)
	@Enumerated
	public Priority getPriority() {
		return priority;
	}

	public void setPriority(Priority priority) {
		this.priority = priority;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final Alarm o = (Alarm) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.alarmDate, o.alarmDate)
				.append(this.description, o.description)
				.append(this.priority, o.priority)
				.append(this.source, o.source)
				.append(this.sourceId, o.sourceId)
				.append(this.status, o.status)
				.append(this.user, o.user)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder().
			append(alarmDate).append(description).
			append(id).append(priority).
			append(source).append(sourceId).
			append(status).append(user).
			toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}

}