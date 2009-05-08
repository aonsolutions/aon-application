package com.code.aon.messaging;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;

@Entity
@Table(name="message_log")
public class Message implements ITransferObject {

	private static final long serialVersionUID = 3549595331221066702L;

	private Integer id;
	private String messageId;
	private MessageContent content;
	private String recipient;
	private String type;
	private Date sentDate;
	private Integer messageParts;
	private String username;

	@Id
	@GeneratedValue
	@Column(nullable=false)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@ManyToOne(cascade = {CascadeType.ALL} )
	@Cascade( {org.hibernate.annotations.CascadeType.SAVE_UPDATE} )
	@JoinColumn(name="message_content")
	@ForeignKey(name = "FK_MESSAGE_LOG_MESSAGE_CONTENT")
	@Index(name = "IDX_MESSAGE_LOG_MESSAGE_CONTENT")					
	public MessageContent getContent() {
		return content;
	}

	public void setContent(MessageContent content) {
		this.content = content;
	}

	@Column(name="message_id", length=64, nullable=false)
	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	@Column(length=64, nullable=false)
	public String getRecipient() {
		return recipient;
	}

	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}

	@Column(length=10)
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Column(name="sent_date", nullable=false)
	public Date getSentDate() {
		return sentDate;
	}

	public void setSentDate(Date sentDate) {
		this.sentDate = sentDate;
	}

	@Column(name="message_parts", nullable=false)
	public Integer getMessageParts() {
		return messageParts;
	}

	public void setMessageParts(Integer messageParts) {
		this.messageParts = messageParts;
	}

	@Column(length=32, nullable=false)
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final Message o = (Message) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.content, o.content)
				.append(this.messageId, o.messageId)
				.append(this.messageParts, o.messageParts)
				.append(this.recipient, o.recipient)
				.append(this.sentDate, o.sentDate)
				.append(this.type, o.type)
				.append(this.username, o.username)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(content)
			.append(id)
			.append(messageId)
			.append(messageParts)
			.append(recipient)
			.append(sentDate)
			.append(type)
			.append(username)
			.toHashCode();
	}
	
	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}
	
}
