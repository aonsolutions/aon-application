package com.code.aon.amazon.apis.sessionInfo;

import java.util.Hashtable;

public class SessionUserInfo {
	
	private  String domain;
	private  String username;
	private  Boolean isAmazonSession;
	private  Hashtable<String,AmazonUser> amazonUsers = new Hashtable<String, AmazonUser>();
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
}

