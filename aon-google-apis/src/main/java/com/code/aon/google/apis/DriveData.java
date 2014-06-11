package com.code.aon.google.apis;

import java.io.InputStream;
import java.util.Vector;

import com.esferalia.aon.google.sql.AbstractSQL.DomainGserviceaccount;
import com.esferalia.aon.google.sql.AbstractSQL.Rattach;


public class DriveData{
	
	private DomainGserviceaccount Gservice;
	private Vector<Rattach> rattachs;
	private String domain;

	
	public DriveData(DomainGserviceaccount Gservice,Vector<Rattach> rattachs){
		this.Gservice=Gservice;
		this.rattachs=rattachs;
		
	}
	
	public DriveData(){
		
	}
	
	public DomainGserviceaccount getGservice(){
		return Gservice;
	}
	
	public void setGservice(DomainGserviceaccount Gservice){
		this.Gservice=Gservice;
	}
	
	public Vector<Rattach> getRattachs(){
		return rattachs;
	}
	
	
	public void setRattachs(Vector<Rattach> rattachs){
		this.rattachs=rattachs;
	}
	
	public String getDomain(){
		return domain;
	}
	
	
	public void setDomain(String domain){
		this.domain=domain;
	}
	}
