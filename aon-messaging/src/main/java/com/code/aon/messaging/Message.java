package com.code.aon.messaging;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.MessageDB;

@Entity
@Table(name="message_log")
public class Message extends MessageDB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

}
