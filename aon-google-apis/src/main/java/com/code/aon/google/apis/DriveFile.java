package com.code.aon.google.apis;

import java.io.Serializable;

import com.code.aon.AonVersion;

public class DriveFile implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private String url, description,mimetype,id, name;
	
	
	public DriveFile(String id,String url, String description, String mimetype) {
		this.id=id;
		this.url = url;
		this.description = description;
		this.name = description.length()> 15 ? description.substring(0,15) + "..." : description;
		this.mimetype = mimetype;
		
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getUrl() {
		return url;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getMimetype(){
		return mimetype;
	}
	
	public void setMimetype(String mimetype){
		this.mimetype = mimetype;
	}
	
	public String getId(){
		return id;
	}
	
	public void setId(String id){
		this.id=id;
	}
}
