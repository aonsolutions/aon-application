package com.code.aon.aio.controller;

import java.io.Serializable;
import java.util.Date;

import com.code.aon.AonVersion;

public class DashboardRecentFiles implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	String name;
	String type;
	String category;
	String	date;
	long size;

	
	public DashboardRecentFiles(){
		//Constructor
	}
	public DashboardRecentFiles(String name, String type , String  category , long size, String date) {
		this.type = type;
		this.name = name;
		this.size = size;
		this.date = date;
		this.category = category;	
	}
	
	public void settype(String type) {
		this.type = type;
	}
	public void setname(String name) {
		this.name = name;
	}
	public void setsize(long size) {
		this.size = size;
	}
	public void setcategory(String category){
		this.category = category;
	}
	public String gettype() {
		return type;		
	}
	public String getname() {
		return name;
	}
	public long getsize() {
		return size;
	}
	public String getcategory(){
		return category;
	}
	public String getDate() {
		
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	

}
