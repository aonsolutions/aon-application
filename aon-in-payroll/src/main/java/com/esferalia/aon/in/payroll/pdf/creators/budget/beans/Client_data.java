package com.esferalia.aon.in.payroll.pdf.creators.budget.beans;

import java.util.Optional;

public class Client_data {

	private Optional<String> business_name;
	private Optional<String> address;
	private Optional<String> nif;
	private Optional<String> city;
	private Optional<String> postal_code;
	private Optional<String> province;
	private Optional<String> phone;
	private Optional<String> mobile;
	private Optional<String> email;
	private Optional<String> contact;
	
	public Client_data(Optional<String> business_name, Optional<String> address, Optional<String> nif, Optional<String> city, Optional<String> postal_code,
			Optional<String> province, Optional<String> phone, Optional<String> mobile, Optional<String> email, Optional<String> contact) {
		super();
		this.business_name = business_name;
		this.address = address;
		this.nif = nif;
		this.city = city;
		this.postal_code = postal_code;
		this.province = province;
		this.phone = phone;
		this.mobile = mobile;
		this.email = email;
		this.contact = contact;
	}

	public Optional<String> getBusiness_name() {return business_name;}
	public Optional<String> getAddress() {return address;}
	public Optional<String> getNif() {return nif;}
	public Optional<String> getCity() {return city;}
	public Optional<String> getPostal_code() {return postal_code;}
	public Optional<String> getProvince() {return province;}
	public Optional<String> getPhone() {return phone;}
	public Optional<String> getMobile() {return mobile;}
	public Optional<String> getEmail() {return email;}
	public Optional<String> getContact() {return contact;}	
	
}
