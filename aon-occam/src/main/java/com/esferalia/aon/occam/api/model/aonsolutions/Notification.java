package com.esferalia.aon.occam.api.model.aonsolutions;

import java.util.Date;
import java.util.LinkedList;
import org.json.JSONArray;
import org.json.JSONObject;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.type.Priority;

public class Notification {

	private Integer id;
	private Domain domain;
	private Date date;
	private String title;
	private String body;
	private NotificationSource source;
	private Integer sourceId;
	private byte[] sender;
	private Priority priority;
	private NotificationReceiver receiver;
	
	public Notification() {}
	
	public Integer getId() {
		return id;
	}
	public Notification setId(Integer id) {
		this.id = id;
		return this;
	}
	public Domain getDomain() {
		return domain;
	}
	public Notification setDomain(Domain domain) {
		this.domain = domain;
		return this;
	}
	public Date getDate() {
		return date;
	}
	public Notification setDate(Date date) {
		this.date = date;
		return this;
	}
	public String getTitle() {
		return title;
	}
	public Notification setTitle(String title) {
		this.title = title;
		return this;
	}
	public String getBody() {
		return body;
	}
	public Notification setBody(String body) {
		this.body = body;
		return this;
	}
	public NotificationSource getSource() {
		return source;
	}
	public Notification setSource(NotificationSource source) {
		this.source = source;
		return this;
	}
	public Integer getSourceId() {
		return sourceId;
	}
	public Notification setSourceId(Integer sourceId) {
		this.sourceId = sourceId;
		return this;
	}
	public byte[] getSender() {
		return sender;
	}
	public Notification setSender(byte[] sender) {
		this.sender = sender;
		return this;
	}

	public Priority getPriority() {
		return priority;
	}
	
	public Notification setPriority(Priority priority) {
		this.priority = priority;
		return this;
	}
	
	public NotificationReceiver getReceiver() {
		return receiver;
	}

	public Notification setReceiver(NotificationReceiver receiver) {
		this.receiver = receiver;
		return this;
	}
	
	
	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		json.put("id", getId());
		json.put("date", getDate().getTime());
		json.put("title", getTitle());
		json.put("body", getBody());
		json.put("source", getSource().name().toLowerCase());
		json.put("source_id", getSourceId());
		json.put("sender", getSender());
		json.put("priority", getPriority().value());
		json.put("status", getReceiver().getStatus());
		json.put("auth", getReceiver().getAuth());
		return json;
	}


}
