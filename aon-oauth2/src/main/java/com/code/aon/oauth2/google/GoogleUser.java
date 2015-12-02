package com.code.aon.oauth2.google;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.tasks.Tasks;

public class GoogleUser {

	private	 String email;
	private  Oauth2 oauth2;
	private  Drive drive;
	private  Tasks tasks;
	private  FileList fl;
	private  String state;
	private  Gmail gmail;
	
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
	
	public String getEmail(){
		return email;
	}
	
	public void setEmail(String email){
		this.email= email;
	}
	
	public FileList getFl(){
		return fl;
	}
	
	public void setFl(FileList fl){
		this.fl= fl;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
	public Gmail getGmail(){
		return gmail;
	}
	public void setGmail(Gmail gmail){
		this.gmail = gmail;
	}
	
}
