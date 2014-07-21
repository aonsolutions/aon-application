package com.code.aon.google.apis.sessionInfo;

import com.google.api.services.drive.Drive;
import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.tasks.Tasks;

public class GoogleUser {

	private	 String gmail;
	private  Oauth2 oauth2;
	private  Drive drive;
	private  Tasks tasks;
	
	public GoogleUser() {
		// TODO Apéndice de constructor generado automáticamente
	}
	
	public Oauth2 getOAuth2(){
		return oauth2;
	}
	
	public void setOAuth2(Oauth2 oauth2){
		this.oauth2=oauth2;
	}

	public Drive getDrive(){
		return drive;
	}

	public void setDrive(Drive drive){
		this.drive= drive;
	}
	
	public Tasks getTasks(){
		return tasks;
	}
	
	public void setTasks(Tasks tasks){
		this.tasks= tasks;
	}
	
	public String getGmail(){
		return gmail;
	}
	
	public void setGmail(String gmail){
		this.gmail= gmail;
	}
}
