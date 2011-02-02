package com.code.aon.groupware;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Type;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.config.User;
import com.code.aon.config.WorkGroup;
import com.code.aon.groupware.enumeration.NoticeStatus;
import com.code.aon.groupware.enumeration.NoticeType;
import com.code.aon.groupware.enumeration.Priority;

@Entity
@Table(name="notice")
public class Notice implements ITransferObject {

	private static final long serialVersionUID = 8420760868787119520L;

	private Integer id;
	
	private Date date;
	
	private User sender;
	
	private WorkGroup workGroup;
	
	private User recipient;
	
	private String source;
	
	private String company;
	
	private String phone;
	
	private String subject;
	
	private NoticeStatus status;
	
	private NoticeType type;
	
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

    @Column(name="date", nullable=false)   
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@ManyToOne
	@JoinColumn(name="sender", nullable=false)
	@Fetch(FetchMode.JOIN)
	@ForeignKey(name = "FK_NOTICE_SENDER")
	@Index(name = "IDX_NOTICE_SENDER")					
	public User getSender() {
		return sender;
	}

	public void setSender(User sender) {
		this.sender = sender;
	}

	@ManyToOne
	@JoinColumn(name="work_group")
	@ForeignKey(name = "FK_NOTICE_WORK_GROUP")
	@Index(name = "IDX_NOTICE_WORK_GROUP")							
	public WorkGroup getWorkGroup() {
		return workGroup;
	}

	public void setWorkGroup(WorkGroup workGroup) {
		this.workGroup = workGroup;
	}

	@ManyToOne
	@JoinColumn(name="recipient")
	@Fetch(FetchMode.JOIN)
	@ForeignKey(name = "FK_NOTICE_RECIPIENT")
	@Index(name = "IDX_NOTICE_RECIPIENT")						
	public User getRecipient() {
		return recipient;
	}

	public void setRecipient(User recipient) {
		this.recipient = recipient;
	}

	@Column(length=64)
	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	@Column(length=64)
	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	@Column(length=16)
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	@Lob
	@Type(type="stringClob")
	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	@Column(nullable=false)
	public NoticeStatus getStatus() {
		return status;
	}

	public void setStatus(NoticeStatus status) {
		this.status = status;
	}

	@Column(nullable=false)
	public NoticeType getType() {
		return type;
	}

	public void setType(NoticeType type) {
		this.type = type;
	}

	@Column(nullable=false)
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
		final Notice o = (Notice) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.company, o.company)
				.append(this.date, o.date)
				.append(this.phone, o.phone)
				.append(this.priority, o.priority)				
				.append(this.recipient, o.recipient)
				.append(this.sender, o.sender)
				.append(this.source, o.source)
				.append(this.status, o.status)
				.append(this.subject, o.subject)
				.append(this.type, o.type)
				.append(this.workGroup, o.workGroup)				
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(this.company)
			.append(this.date)
			.append(this.id)
			.append(this.phone)
			.append(this.priority)				
			.append(this.recipient)
			.append(this.sender)
			.append(this.source)
			.append(this.status)
			.append(this.subject)
			.append(this.type)
			.append(this.workGroup)				
			.toHashCode();		
	}	

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}
	
}