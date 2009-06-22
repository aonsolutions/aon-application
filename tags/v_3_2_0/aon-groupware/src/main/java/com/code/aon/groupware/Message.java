package com.code.aon.groupware;

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
import org.hibernate.annotations.Cascade;

import com.code.aon.common.ITransferObject;

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
	public MessageContent getContent() {
		return content;
	}

	public void setContent(MessageContent content) {
		this.content = content;
	}

	@Column(name="message_id", nullable=false)
	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public String getRecipient() {
		return recipient;
	}

	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}

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

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
    		return super.equals(obj);
		}
		if (obj instanceof Message) {
			Message o = (Message) obj;
			if (o.getId() == null && id == null) {
				return super.equals(obj);	
			}
			if (ObjectUtils.equals(getId(), o.getId())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		return (this.id != null) ? id.hashCode() : super.hashCode();
	}	
	
}
