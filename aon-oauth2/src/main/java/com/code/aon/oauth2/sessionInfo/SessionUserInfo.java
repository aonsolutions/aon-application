package com.code.aon.oauth2.sessionInfo;

import java.util.Hashtable;

import com.code.aon.oauth2.amazon.AmazonUser;
import com.code.aon.oauth2.google.GoogleUser;

public class SessionUserInfo {
	
	private  String domain;
	private  String username;
	private  Boolean isAmazonSession;
	private  Hashtable<String,AmazonUser> amazonUsers = new Hashtable<String, AmazonUser>();
	private Boolean isGoogleSession;
	private Hashtable<String, GoogleUser> googleUsers = new Hashtable<String, GoogleUser>();
	
	public SessionUserInfo(){
		
	}
	
	public Hashtable<String, AmazonUser> getAmazonUsers(){
		return amazonUsers;
	}

	public void setAmazonUsers(Hashtable<String, AmazonUser> amazonUsers){
		this.amazonUsers=amazonUsers;
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
	
	public Boolean getIsAmazonSession(){
		return isAmazonSession;
	}
	
	public void setIsAmazonSession(Boolean isAmazonSession){
		this.isAmazonSession= isAmazonSession;
	}

	public Boolean getIsGoogleSession() {
		return isGoogleSession;
	}

	public void setIsGoogleSession(Boolean isGoogleSession) {
		this.isGoogleSession = isGoogleSession;
	}

	public Hashtable<String, GoogleUser> getGoogleUsers() {
		return googleUsers;
	}

	public void setGoogleUsers(Hashtable<String, GoogleUser> googleUsers) {
		this.googleUsers = googleUsers;
	}
	
	
}

