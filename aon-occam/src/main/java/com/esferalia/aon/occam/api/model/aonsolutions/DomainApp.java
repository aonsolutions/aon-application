package com.esferalia.aon.occam.api.model.aonsolutions;

import org.json.JSONObject;

public class DomainApp {
	
	private Integer id;
	private Integer domain;
	private AonApp app;
	private Boolean active;
	
	public DomainApp() {
	
	}

	public Integer getId() {
		return id;
	}

	public DomainApp setId(Integer id) {
		this.id = id;
		return this;
	}

	public Integer getDomain() {
		return domain;
	}

	public DomainApp setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}

	public AonApp getApp() {
		return app;
	}

	public DomainApp setApp(AonApp app) {
		this.app = app;
		return this;
	}

	public Boolean isActive() {
		return active;
	}
	
	public Boolean getActive() {
		return active;
	}

	public DomainApp setActive(Boolean active) {
		this.active = active;
		return this;
	}
	
	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		json.put("id", getId());
		json.put("domain", getDomain());
		json.put("app", getApp().name());
		json.put("active", getActive());
		return json;
	}
	

}
