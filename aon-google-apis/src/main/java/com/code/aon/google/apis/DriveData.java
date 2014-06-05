package com.code.aon.google.apis;

import java.io.InputStream;


public class DriveData{
	
	private String ID;
	private String MIMETYPE;
	private String TITLE;
	private InputStream FILE;
	
	public DriveData(String Id,String Mimetype,String Title){
		ID=Id;
		MIMETYPE=Mimetype;
		TITLE=Title;
	}
	
	public DriveData(){
		
	}
	
	public String getId(){
		return ID;
	}
	
	public void setId(String id){
		ID=id;
	}
	
	public String getMimeType(){
		return MIMETYPE;
	}
	
	public void setMimeType(String mimetype){
		MIMETYPE=mimetype;
	}
	
	public String getTitle(){
		return TITLE;
	}
	
	public void setTitle(String title){
		TITLE=title;
	}

	public InputStream getFile(){
		return FILE;
	}
	
	public void setFile(InputStream file){
		FILE=file;
	}
}
