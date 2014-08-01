package com.code.aon.google.apis.sessionInfo;



import java.util.Hashtable;

public class SessionUserInfo {
	
	private  String domain;
	private  String username;
	private  Boolean isGoogleSession;
	private  Hashtable<String,GoogleUser> googleUsers = new Hashtable<String, GoogleUser>();
	public SessionUserInfo(){
		
	}
	
	public Hashtable<String, GoogleUser> getGoogleUsers(){
		return googleUsers;
	}

	public void setGoogleUsers(Hashtable<String, GoogleUser> googleUsers){
		this.googleUsers=googleUsers;
	}
	
	public String getUsername(){
		return username;
	}
	
	public void setUsername(String username){
		this.username= username;
	}
	
	public String getDomain(){
		return domain;
	}
	
	public void setDomain(String domain){
		this.domain= domain;
	}
	
	public Boolean getIsGoogleSession(){
		return isGoogleSession;
	}
	
	public void setIsGoogleSession(Boolean isGoogleSession){
		this.isGoogleSession= isGoogleSession;
	}
}
