package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.Date;

public class Employee implements Serializable, HasId<Integer> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2517702556287850026L;

	private int id;

	private int person;

	private String name;
	private String firstSurname;
	private String secondSurname;

	private Date startDate;
	private Date endDate;

	private String document;

	public Integer getId() {
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

	public String getDocument() {
		return document;
	}

	public void setDocument(String document) {
		this.document = document;
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

		if (firstSurname != null) {
			sb.append(firstSurname.trim());
		}
		if (secondSurname != null) {
			if (sb.length() > 0) {
				sb.append(" ");
			}
			sb.append(secondSurname.trim());
		}
		if (sb.length() > 0) {
			sb.append(", ");
		}
		sb.append(getName());

		return sb.toString();
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	@Override
	public int hashCode() {
		return id;
	}

	@Override
	public boolean equals(Object obj) {
		return ( obj != null) && ( obj instanceof Employee)
				&& ( id == ((Employee) obj).id );
	}
	

}
