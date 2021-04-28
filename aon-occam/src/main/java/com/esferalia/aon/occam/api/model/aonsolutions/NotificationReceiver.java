package com.esferalia.aon.occam.api.model.aonsolutions;

import java.io.Serializable;

import org.json.JSONObject;
import com.esferalia.aon.occam.api.model.Domain;

public class NotificationReceiver implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private byte[] auth;
	private Domain domain;
	private NotificationStatus status;
	
	public NotificationReceiver() {}
	
	public Integer getId() {
		return id;
	}
	
	public byte[] getAuth() {
		return auth;
	}
	
	public Domain getDomain() {
		return domain;
	}
	
	public NotificationStatus getStatus() {
		return status;
	}
	
	public NotificationReceiver setId(Integer id) {
		this.id = id;
		return this;
	}
	
	public NotificationReceiver setAuth(byte[] auth) {
		this.auth = auth;
		return this;
	}
	
	public NotificationReceiver setStatus(NotificationStatus status) {
		this.status = status;
		return this;
	}

	public NotificationReceiver setDomain(Domain domain) {
		this.domain = domain;
		return this;
	}


	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		json.put("id", getId());
		json.put("domain", getDomain());
		json.put("auth", getAuth());
		json.put("status", NotificationStatus.value(getStatus()));
		return json;
	}

}

