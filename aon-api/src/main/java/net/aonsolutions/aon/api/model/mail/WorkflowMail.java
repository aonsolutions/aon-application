package net.aonsolutions.aon.api.model.mail;

import com.esferalia.aon.watson.server.AonDateUtils;
import java.util.Date;

public class WorkflowMail{
	String name;
	String date;
	String message;
	public WorkflowMail() {}

	public String getName() {
		return name;
	}
	
	public String getDate() {
		return date;
	}
	
	public String getMessage() {
		return message;
	}

	public WorkflowMail setName(String name) {
		this.name = name;
		return this;
	}
	
	public WorkflowMail setDate(Date date) {
		this.date = AonDateUtils.format(date, "dd/MM/yyyy H:m");
		return this;
	}
	public WorkflowMail setMessage(String message) {
		this.message = message;
		return this;
	}
}
