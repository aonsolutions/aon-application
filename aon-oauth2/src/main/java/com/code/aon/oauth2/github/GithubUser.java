package com.code.aon.oauth2.github;

public class GithubUser {

	String user_id;
	String name;
	String email;
	String state;
	String acces_token;
	
	public GithubUser(String user_id,String name,String email) {
		this.user_id = user_id;
		this.name = name;
		this.email = email;
	}
	public String getUser_id() {
		return user_id;
	}
	public GithubUser setUser_id(String user_id) {
		this.user_id = user_id;
		return this;
	}
	public String getName() {
		return name;
	}
	public GithubUser setName(String name) {
		this.name = name;
		return this;
	}
	public String getEmail() {
		return email;
	}
	public GithubUser setEmail(String email) {
		this.email = email;
		return this;
	}
	public String getState() {
		return state;
	}
	public GithubUser setState(String state) {
		this.state = state;
		return this;
	}
	public String getAcces_token() {
		return acces_token;
	}
	public GithubUser setAcces_token(String acces_token) {
		this.acces_token = acces_token;
		return this;
	}
	
	
}
