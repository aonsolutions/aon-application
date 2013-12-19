package com.esferalia.aon.gwt.fiscal.shared;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

@SuppressWarnings("serial")
public class Enterprise implements Serializable, IsSerializable {

	private Integer id;
	private int domain;
	private String document;
	private String surname;
	private String name;

	// private boolean status;
	// private String contact_person;
	// private String contact_phone;
	// private boolean complementary;
	// private int number;
	// private int replaced_number;
	// private int receiver_count_total;
	// private double receipt_total;
	// private double retention_total;

	public Enterprise() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public int getDomain() {
		return domain;
	}

	public void setDomain(int domain) {
		this.domain = domain;
	}

	public String getDocument() {
		return document;
	}

	public void setDocument(String document) {
		this.document = document;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	// ------------------------------------------------------------ Ops. Methods
	public String alert() {
		return "id ..: " + getId() + "\n" + "domain ..: " + getDomain() + "\n"
				+ "document ..: " + getDocument() + "\n" + "surname ..: "
				+ getSurname() + "\n" + "name ..: " + getName() + "\n";
	}

}
