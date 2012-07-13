package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;


public class Employee implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2517702556287850026L;
	
	private int id;
	
	private int person;

	private String name ;
	private String firstSurname;
	private String secondSurname;
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getPerson() {
		return person;
	}
	
	public void setPerson(int personId) {
		this.person = personId;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getFirstSurname() {
		return firstSurname;
	}

	public void setFirstSurname(String firstSurname) {
		this.firstSurname = firstSurname;
	}
	
	public String getSecondSurName() {
		return secondSurname;
	}
	
	public void setSecondSurName(String secondSurName) {
		this.secondSurname = secondSurName;
	}
	
    public String getFullname() {
    	StringBuffer sb = new StringBuffer();
    	
		if (firstSurname != null  ) {
			sb.append(firstSurname.trim());
		}
		if (secondSurname != null  ) {
			if ( sb.length() > 0 ) {
				sb.append(" ");
			}
			sb.append(secondSurname.trim());
		}
		if ( sb.length() > 0 ) {
			sb.append(", ");
		}
		sb.append(getName());
		
		return sb.toString();
    }
	
	

}
