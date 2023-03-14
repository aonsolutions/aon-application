package com.code.aon.jaas.auth.spi.db;

public class User extends BasicInfo {

	private String login;
	
	private String password;
	
	private Integer domain;

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Integer getDomain() {
		return domain;
	}

	public void setDomain(Integer domain) {
		this.domain = domain;
	}
	
}
