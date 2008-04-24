package com.code.aon.dao.ldap;

public class PropertyInfo {

	private String name;
	
	private String ldapName;
	
	private int length;
	
	private boolean nullable;

	public PropertyInfo(String name, String ldapName, int length, boolean nullable) {
		this.name = name;
		this.ldapName = ldapName;
		this.length = length;
		this.nullable = nullable;
	}

	public String getName() {
		return name;
	}

	public String getLdapName() {
		return ldapName;
	}

	public int getLength() {
		return length;
	}

	public boolean isNullable() {
		return nullable;
	}
	
}
