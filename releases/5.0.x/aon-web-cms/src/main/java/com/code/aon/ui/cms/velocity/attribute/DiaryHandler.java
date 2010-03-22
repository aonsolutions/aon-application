package com.code.aon.ui.cms.velocity.attribute;

public class DiaryHandler {

	private int day;
	
	private String url;
	
	public DiaryHandler (int day, String url) {
		this.day = day;
		this.url = url;
	}

	public int getDay() {
		return day;
	}

	public String getUrl() {
		return url;
	}
	
	public boolean isLink(){
		return url!=null?true:false;
	}
}
