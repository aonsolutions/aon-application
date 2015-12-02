package com.code.aon.oauth2.sessionInfo;

import java.util.Hashtable;

public class SessionEnterpriseInfo {

	private  Hashtable<String,SessionUserInfo> users=new Hashtable<String, SessionUserInfo>();
	private  String domain;
	private  String action;
	
	public SessionEnterpriseInfo(){
		
	}
	
	public Hashtable<String, SessionUserInfo> getUsers(){
		return users;
	}
	
	public void setUsers(Hashtable<String, SessionUserInfo> users){
		this.users=users;
	}
	
	public String getDomain(){
		return domain;
	}

	public void setDomain(String domain){
		this.domain= domain;
	}
	
	public String getAction(){
		return action;
	}

	public void setAction(String action){
		this.action=action;
	}
	
}
