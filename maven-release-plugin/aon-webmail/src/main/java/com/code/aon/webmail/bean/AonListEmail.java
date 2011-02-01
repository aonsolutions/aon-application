package com.code.aon.webmail.bean;

public class AonListEmail {

	private String Email;
	
	private boolean selected;
	
	private boolean added;

	public AonListEmail(String email) {
		super();
		Email = email;
		this.selected = false;
		this.added = false;
	}

	public String getEmail() {
		return Email;
	}

	public void setEmail(String email) {
		Email = email;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public boolean isAdded() {
		return added;
	}

	public void setAdded(boolean added) {
		this.added = added;
	}
	
}
