package com.code.aon.messaging;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.MessageContentDB;

@Entity
@Table(name="message_content")
public class MessageContent extends MessageContentDB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
}