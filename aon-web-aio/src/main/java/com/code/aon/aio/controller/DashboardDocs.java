package com.code.aon.aio.controller;

import java.io.Serializable;

import com.code.aon.AonVersion;

public class DashboardDocs implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	String category;
	String categoryAbr;
	String type;
	String typeAbr;
	int num;
	String description;
	long size;
	long mediaDrive;
	int color;
	
	public DashboardDocs(){
		//Constructor
	}
	public DashboardDocs(String type, int num , long size) {
		this.type = type;
		this.num = num;
		this.size = size;
		this.typeAbr= type.substring(0, 3);
	}
	
	public DashboardDocs(String category,int num , long size,int aux) {
		
		this.category = category;
		this.num = num;
		this.size = size;
		this.categoryAbr= category.substring(0, 3);
		
	}
	
	public void settype(String type) {
		this.type = type;
		this.typeAbr= type.substring(0, 3);
	}
	public void setnum(int num) {
		this.num = num;
	}
	public void setsize(long size) {
		this.size = size;
	}
	public String gettype() {
		return type;		
	}
	public int getnum() {
		return num;
	}
	public long getsize() {
		return size;
	}
	public long getmediaDrive() {
		return mediaDrive;
	}
	public void setmediaDrive(long mediaDrive, int num) {
		this.mediaDrive=0;
		
	}
	
	public String gettypeAbr() {
		return typeAbr;
	}
	public void settypeAbr() {
		this.typeAbr=type.substring(0, 3);
	}
	
	public Integer getColor(){
		return color;
	}
	
	public void setColor(int color){
		this.color=color;
	}
	
	public String getDescription(){
		if (type != null)
			return "<b>Tipo:</b>"+type+"<br/> <b>Archivos:</b>"+num+"<br/> <b>Tamaño:</b>" +size;
		else if(category !=null)
			return "<b>Categoría:</b>"+category+"<br/> <b>Archivos:</b>"+num;//+"<br/> <b>Tamaño:</b>" +size;
		else return null;
		
	}
	
	public void setDescription(String numString){
		this.description=numString;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
		this.categoryAbr= category.substring(0, 3);
	}
	
	public String getcategoryAbr() {
		return categoryAbr;
	}
	public void setcategoryAbr() {
		this.categoryAbr=category.substring(0, 3);
	}
	

}
