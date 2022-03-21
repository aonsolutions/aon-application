package net.aonsolutions.aon.api.servlet.booking;

import java.util.List;

public class BookingMail {

	String user;
	String domainName;
	String companyName;
	String parentName;
	String domainType;
	int numberOfUsers;
	List<String> apps;
	
	public String getUser() {
		return user;
	}
	
	public BookingMail setUser(String user) {
		this.user = user;
		return this;
	}
	
	public String getDomainName() {
		return domainName;
	}
	
	public BookingMail setDomainName(String domainName) {
		this.domainName = domainName;
		return this;
	}
	
	public String getCompanyName() {
		return companyName;
	}
	
	public BookingMail setCompanyName(String companyName) {
		this.companyName = companyName;
		return this;
	}
	
	public String getParentName() {
		return parentName;
	}
	
	public BookingMail setParentName(String parentName) {
		this.parentName = parentName;
		return this;
	}
	
	public String getDomainType() {
		return domainType;
	}
	
	public BookingMail setDomainType(String domainType) {
		this.domainType = domainType;
		return this;
	}
	
	public int getNumberOfUsers() {
		return numberOfUsers;
	}
	
	public BookingMail setNumberOfUsers(int numberOfUsers) {
		this.numberOfUsers = numberOfUsers;
		return this;
	}

	public List<String> getApps() {
		return apps;
	}
	
	public BookingMail setApps(List<String> apps) {
		this.apps = apps;
		return this;
	}
	
}
