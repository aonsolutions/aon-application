package com.esferalia.aon.occam.api.model;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;

public class LogData {

	int id;
	int domain;
	Date date;
	String message;
	
	public LogData() {
		super();
	}
	
	public LogData(int domain, Exception e) {
		super();
		this.date = new Date();
		this.domain = domain;
		StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String stackTrace = sw.toString();
		this.message ="Message : " + e.getMessage() + "\n" + "Cause : " + e.getCause() + "\n" + "Class : "  + e.getClass()+ "\n" + "Stacktrace : "  + stackTrace + "\n";
	}
	
	public LogData(int domain, String msg) {
		super();
		this.date = new Date();
		this.domain = domain;
		this.message = msg;
	}
	
	public int getId() {
		return id;
	}
	
	public LogData setId(int id) {
		this.id = id;
		return this;
	}
	
	public int getDomain() {
		return domain;
	}
	
	public LogData setDomain(int domain) {
		this.domain = domain;
		return this;
	}
	
	public Date getDate() {
		return date;
	}
	
	public LogData setDate(Date date) {
		this.date = date;
		return this;
	}
	
	public String getMessage() {
		return message;
	}
	
	public LogData setMessage(String message) {
		this.message = message;
		return this;
	}
	
	
	
}