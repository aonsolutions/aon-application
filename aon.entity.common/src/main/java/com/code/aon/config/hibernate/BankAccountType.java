package com.code.aon.config.hibernate;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang.StringUtils;
import org.hibernate.dialect.Dialect;
import org.hibernate.type.StringType;

import com.code.aon.AonVersion;
import com.code.aon.common.enumeration.Country;
import com.code.aon.config.BankAccount;

public class BankAccountType extends StringType {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Override
	public Class<?> getReturnedClass() {
		return BankAccount.class;
	}
	
	@Override
	public String getName() { 
		return "bankAccount"; 
	}
	
	@Override
	public Object fromStringValue(String xml) {
		String value = StringUtils.trimToNull(xml);
		if (value == null) {
			return null;
		} else value = value.toUpperCase();

		BankAccount bankAccount = new BankAccount();
		bankAccount.setCountry(Country.valueOf(StringUtils.substring(value, 0, 2)));
		bankAccount.setCheck(StringUtils.substring(value, 2, 4));
		bankAccount.setBban1(StringUtils.substring(value, 4, 8));
		bankAccount.setBban2(StringUtils.substring(value, 8, 12));
		bankAccount.setBban3(StringUtils.substring(value, 12, 16));
		bankAccount.setBban4(StringUtils.substring(value, 16, 20));
		bankAccount.setBban5(StringUtils.substring(value, 20, 24));
		bankAccount.setBban6(StringUtils.substring(value, 24, 28));
		bankAccount.setBban7(StringUtils.substring(value, 28, 32));
		bankAccount.setBban8(StringUtils.substring(value, 32, 34));
		return bankAccount;
	}

	@Override
	public String toString(Object value) {
		if (value == null) {
			return null;
		}
		if (value instanceof String) {
			return (String)value;
		}
		return ((BankAccount)value).getIban();
	}
	
	@Override
	public String objectToSQLString(Object value, Dialect dialect) throws Exception {
		return '\'' + toString(value) + '\'';
	}

	@Override
	public void set(PreparedStatement st, Object value, int index) throws SQLException {
		super.set(st, toString(value), index);
	}
	
	@Override
	public Object get(ResultSet rs, String name) throws SQLException {
		String account = (String) super.get(rs, name);
		return fromStringValue(account);
	}

}
