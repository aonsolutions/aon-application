package com.esferalia.aon.in.payroll.pdf.maker.budget.beans;

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
	
	public Client_data(String business_name,String address,String nif,String city,String postal_code,String province, String phone, String mobile, String email, String contact) {
		super();
		this.business_name = 	Optional.ofNullable(business_name);
		this.address = 			Optional.ofNullable(address);
		this.nif = 				Optional.ofNullable(nif);
		this.city = 			Optional.ofNullable(city);
		this.postal_code = 		Optional.ofNullable(postal_code);
		this.province = 		Optional.ofNullable(province);
		this.phone = 			Optional.ofNullable(phone);
		this.mobile = 			Optional.ofNullable(mobile);
		this.email = 			Optional.ofNullable(email);
		this.contact = 			Optional.ofNullable(contact);
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

	@Override
	public String toString() {
		return "Client_data [business_name=" + business_name + ", address=" + address + ", nif=" + nif + ", city="
				+ city + ", postal_code=" + postal_code + ", province=" + province + ", phone=" + phone + ", mobile="
				+ mobile + ", email=" + email + ", contact=" + contact + "]";
	}	
	
	
	
}
