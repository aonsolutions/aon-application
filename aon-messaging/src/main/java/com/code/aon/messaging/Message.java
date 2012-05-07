package com.code.aon.messaging;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.esferalia.aon.entity.master.MessageDB;

@Entity
@Table(name="message_log")
public class Message extends MessageDB {

	private static final long serialVersionUID = 1L;

}
