package net.aonsolutions.aon.api.model.mail;

public class TaskMail {
	String number;
	String date;
	String url; 
	String title;
	String logo;
	
	public TaskMail() {
	}

	public String getNumber() {
		return number;
	}

	public TaskMail setNumber(String number) {
		this.number = number;
		return this;
	}

	public String getDate() {
		return date;
	}

	public TaskMail setDate(String date) {
		this.date = date;
		return this;
	}

	public String getUrl() {
		return url;
	}

	public TaskMail setUrl(String url) {
		this.url = url;
		return this;
	}
	
	public String getTitle() {
		return title;
	}

	public TaskMail setTitle(String title) {
		this.title = title;
		return this;
	}
	
	public String getLogo() {
		return logo;
	}

	public TaskMail setLogo(String logo) {
		this.logo = logo;
		return this;
	}
}
