package com.esferalia.aon.occam.api.model.finance.checkit;

import java.io.Serializable;

public class CheckItLoginFields implements Serializable{

	private static final long serialVersionUID = -2702026805195048593L;
	
	private Integer id;
	private String type;
	private String userID;
	private String userPassword;
	private String userPIN;
	
	private String userIDInput;
	private String userPasswordInput;
	private String userPINInput;
	
	
	
	public Integer getId() {
		return id;
	}
	public CheckItLoginFields setId(Integer id) {
		this.id = id;
		return this;
	}
	public String getType() {
		return type;
	}
	public CheckItLoginFields setType(String type) {
		this.type = type;
		return this;
	}
	public String getUserID() {
		return userID;
	}
	public CheckItLoginFields setUserID(String userID) {
		this.userID = userID;
		return this;
	}
	public String getUserPassword() {
		return userPassword;
	}
	public CheckItLoginFields setUserPassword(String userPassword) {
		this.userPassword = userPassword;
		return this;
	}
	public String getUserPIN() {
		return userPIN;
	}
	public CheckItLoginFields setUserPIN(String userPIN) {
		this.userPIN = userPIN;
		return this;
	}
	@Override
	public String toString() {
		return "type";
	}
	
	public void clone(CheckItLoginFields original) {
		if (original != null) {
			this.setId(original.getId())
			.setType(original.getType())
			.setUserID(original.getUserID())
			.setUserPassword(original.getUserPassword())
			.setUserPIN(original.getUserPIN());			
		} else
			this.clear();
	}
	
	public void clear() {
		this.setId(null)
		.setType(null)
		.setUserID(null)
		.setUserPassword(null)
		.setUserPIN(null);	
	}
	public String getUserIDInput() {
		return userIDInput;
	}
	public CheckItLoginFields setUserIDInput(String userIDInput) {
		this.userIDInput = userIDInput;
		return this;
	}
	public String getUserPasswordInput() {
		return userPasswordInput;
	}
	public CheckItLoginFields setUserPasswordInput(String userPasswordInput) {
		this.userPasswordInput = userPasswordInput;
		
		return this;
	}
	public String getUserPINInput() {
		return userPINInput;
	}
	public CheckItLoginFields setUserPINInput(String userPINInput) {
		this.userPINInput = userPINInput;
		return this;
	}
	
}
