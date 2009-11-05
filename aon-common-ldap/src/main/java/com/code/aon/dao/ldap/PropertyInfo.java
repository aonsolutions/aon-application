package com.code.aon.dao.ldap;

import com.code.aon.common.ITransferObject;

public class PropertyInfo {

	private String accesPath;
	
	private String alias;
	
	private Class<?> propertyClass;
	
	private String ldapName;
	
	private int length;
	
	private boolean nullable;
	
	private String baseDN;

	public PropertyInfo(String accesPath, String ldapName) {
		this.accesPath = accesPath;
		this.ldapName = ldapName;
		this.length = -1;
		this.nullable = true;
	}

	public String getAccesPath() {
		return accesPath;
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

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}

	public Class<?> getPropertyClass() {
		return propertyClass;
	}

	public void setPropertyClass(Class<?> propertyClass) {
		this.propertyClass = propertyClass;
	}

	public boolean isTransferObject() {
		return ITransferObject.class.isAssignableFrom(this.propertyClass);
	}
	
	public String getToAccessPath() {
		int pos = this.accesPath.indexOf('.');
		if ( pos != -1 ) {
			return this.accesPath.substring(0, pos);
		}
		return null;
	}

	public String getBaseDN() {
		return baseDN;
	}

	public void setBaseDN(String baseDN) {
		this.baseDN = baseDN;
	}
	
}
