package net.aonsolutions.occam.api.model;

import java.io.Serializable;

public class User implements Serializable {
	
	private static final long serialVersionUID = -5850188463638573104L;
	
	private Integer id;
	private Integer domain;
	private String name;
	private String login;
	private boolean active;
	
	public Integer getId() {
		return id;
	}
	public User setId(Integer id) {
		this.id = id;
		return this;
	}
	
	public Integer getDomain() {
		return domain;
	}
	public User setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	
	public String getName() {
		return name;
	}
	public User setName(String name) {
		this.name = name;
		return this;
	}
	
	public String getLogin() {
		return login;
	}
	public User setLogin(String login) {
		this.login = login;
		return this;
	}
	
	public boolean isActive() {
		return active;
	}
	public User setActive(boolean active) {
		this.active = active;
		return this;
	}
	
}
