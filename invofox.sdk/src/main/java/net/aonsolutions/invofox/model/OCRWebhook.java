package net.aonsolutions.invofox.model;

import java.io.Serializable;
import java.util.List;

public class OCRWebhook implements Serializable{

	private static final long serialVersionUID = 1L;

	private String id;
	private OCREndpoint endpoint;
	private List<OCREvent> events;
	private OCRSecurity security;
	private boolean active;
	
	public String getId() {
		return id;
	}
	
	public OCRWebhook setId(String id) {
		this.id = id;
		return this;
	}
	
	public OCREndpoint getEndpoint() {
		return endpoint;
	}
	
	public OCRWebhook setEndpoint(OCREndpoint endpoint) {
		this.endpoint = endpoint;
		return this;
	}
	
	public List<OCREvent> getEvents() {
		return events;
	}
	
	public OCRWebhook setEvents(List<OCREvent> events) {
		this.events = events;
		return this;
	}
	
	public OCRSecurity getSecurity() {
		return security;
	}
	
	public OCRWebhook setSecurity(OCRSecurity security) {
		this.security = security;
		return this;
	}
	
	public boolean isActive() {
		return active;
	}
	
	public OCRWebhook setActive(boolean active) {
		this.active = active;
		return this;
	}
	
	
	
}
