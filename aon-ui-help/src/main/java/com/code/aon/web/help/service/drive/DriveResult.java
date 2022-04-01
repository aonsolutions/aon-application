package com.code.aon.web.help.service.drive;

public class DriveResult {

	private String text;
	private String link;
	
	public DriveResult() {
	
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	public String getText() {
		return text;
	}
	
	public void setLink(String link) {
		this.link = link;
	}
	
	public String getLink() {
		return link;
	}
	
	@Override
	public String toString() {
		return this.text;
	}
	
}
