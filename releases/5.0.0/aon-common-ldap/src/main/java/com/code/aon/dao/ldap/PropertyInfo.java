package com.code.aon.dao.ldap;

import java.util.List;

import com.code.aon.common.ITransferObject;

public class PropertyInfo {

	private String accesPath;
	
	private String alias;
	
	private Class<?> propertyClass;
	
	private Class<?> baseClass;
	
	private String ldapName;
	
	private int length;
	
	private boolean nullable;
	
	private String baseDN;
	
	private boolean transferObject;
	
	private boolean collection;

	public PropertyInfo(String accesPath, String ldapName, Class<?> propertyClass) {
		this.accesPath = accesPath;
		this.ldapName = ldapName;
		this.length = -1;
		this.nullable = true;
		this.propertyClass = propertyClass;
		init();
	}
	
	private void init() {
		if ( List.class.isAssignableFrom(this.propertyClass) ) {
			this.collection = true;
		} else {
			setBaseClass( this.propertyClass );
		} 
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

	public Class<?> getBaseClass() {
		return baseClass;
	}

	public void setBaseClass(Class<?> baseClass) {
		this.baseClass = baseClass;
		this.transferObject = ITransferObject.class.isAssignableFrom(this.baseClass);
	}

	public boolean isTransferObject() {
		return transferObject;
	}
	
	public boolean isCollection() {
		return collection;
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
