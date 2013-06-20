package com.code.aon.messaging;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.esferalia.aon.entity.master.MessageContentDB;

@Entity
@Table(name="message_content")
public class MessageContent extends MessageContentDB {

	private static final long serialVersionUID = 1L;
	
}