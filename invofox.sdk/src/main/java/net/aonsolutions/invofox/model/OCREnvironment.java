package net.aonsolutions.invofox.model;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class OCREnvironment implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private String id;
	private String name;
	private List<OCRApiKey> apikeys;
	private List<OCRWebhook> webhooks;

	public String getId() {
		return id;
	}
	
	public OCREnvironment setId(String id) {
		this.id = id;
		return this;
	}
	
	public String getName() {
		return name;
	}
	
	public OCREnvironment setName(String name) {
		this.name = name;
		return this;
	}
	
	public List<OCRApiKey> getApikeys() {
		if(apikeys == null) 
			apikeys = new LinkedList<>();
		return apikeys;
	}
	
	public OCREnvironment setApikeys(List<OCRApiKey> apikeys) {
		this.apikeys = apikeys;
		return this;
	}
	
	public OCREnvironment addApiKey(OCRApiKey apiKey) {
		getApikeys().add(apiKey);
		return this;
	}
	
	public List<OCRWebhook> getWebhooks() {
		if(webhooks == null) 
			webhooks = new LinkedList<>();
		return webhooks;
	}
	
	public OCREnvironment setWebhooks(List<OCRWebhook> webhooks) {
		this.webhooks = webhooks;
		return this;
	}
	
	public OCREnvironment addWebhook(OCRWebhook webhook) {
		getWebhooks().add(webhook);
		return this;
	}
	

}
