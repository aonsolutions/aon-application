package com.code.aon.config.hibernate;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang.StringUtils;
import org.hibernate.dialect.Dialect;
import org.hibernate.type.StringType;

import com.code.aon.config.BankAccount;

public class BankAccountType extends StringType {

	private static final long serialVersionUID = 7916889161980318593L;

	@SuppressWarnings("unchecked")
	public Class getReturnedClass() {
		return BankAccount.class;
	}
	public String getName() { 
			return "bankAccount"; 
	}
	
	@Override
	public Object fromStringValue(String xml) {
		if (xml== null) {
			return null;
		}
		BankAccount ba = new BankAccount();
		ba.setEntity(StringUtils.substring(xml,0,4));
		ba.setOffice(StringUtils.substring(xml,4,8));
		ba.setControl(StringUtils.substring(xml,8,10));
		ba.setAccount(StringUtils.substring(xml,10));
		return ba;
	}
	
	public void set(PreparedStatement st, Object value, int index) throws SQLException {
		super.set(st, toString(value), index);
	}

	public String objectToSQLString(Object value, Dialect dialect) throws Exception {
		BankAccount ba = (BankAccount) value;
		return '\'' + (ba==null?null:ba.getValue()) + '\'';
	}

	public String toString(Object value) {
		if (value instanceof String) {
			return (String) value;
		}
		BankAccount ba = (BankAccount) value;
		return ba==null?null:ba.getValue();
	}

	public Object get(ResultSet rs, String name) throws SQLException {
		String account = rs.getString(name);
		return fromStringValue(account);
	}

}
