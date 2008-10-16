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

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.code.aon.common.ITransferObject;
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
	public User getSender() {
		return sender;
	}

	public void setSender(User sender) {
		this.sender = sender;
	}

	@ManyToOne
	@JoinColumn(name="work_group")
	public WorkGroup getWorkGroup() {
		return workGroup;
	}

	public void setWorkGroup(WorkGroup workGroup) {
		this.workGroup = workGroup;
	}

	@ManyToOne
	@JoinColumn(name="recipient")
	@Fetch(FetchMode.JOIN)
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

	@Column(length=65535)
	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	@Transient
	public String getSubjectHead(){
		if(getSubject() != null && getSubject().length() >= 64){
			return getSubject().substring(0, 63);
		}
		return getSubject();
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
}