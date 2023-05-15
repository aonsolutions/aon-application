package com.esferalia.aon.occam.api.model.aonsolutions;

import java.io.Serializable;

public class DomainApp implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private Integer domain;
	private AonApp app;
	private boolean active;
	

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

	public boolean isActive() {
		return active;
	}

	public DomainApp setActive(Boolean active) {
		this.active = active;
		return this;
	}
	
	public boolean isEmpty() {
		return getId() == null && getApp() == null 
			&& getDomain() == null;
	}

}
