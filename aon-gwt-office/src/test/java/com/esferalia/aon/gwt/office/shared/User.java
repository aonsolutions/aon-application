package com.esferalia.aon.gwt.office.shared;

import java.io.Serializable;

public class User implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String name;
	private String surname;
	private int old;
	
	public User(String name, String surname, int old) {
		this.name = name;
		this.surname = surname;
		this.old = old;
	}
	
	public String getName() {
		return name;
	}
	
	public String getSurname() {
		return surname;
	}
	
	public int getOld() {
		return old;
	}
}
