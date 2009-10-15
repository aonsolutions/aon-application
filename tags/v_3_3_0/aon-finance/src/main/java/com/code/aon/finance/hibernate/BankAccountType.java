package com.code.aon.finance.hibernate;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang.StringUtils;
import org.hibernate.dialect.Dialect;
import org.hibernate.type.StringType;

import com.code.aon.finance.BankAccount;

public class BankAccountType extends StringType {

	private static final long serialVersionUID = 7916889161980318593L;

	@SuppressWarnings("unchecked")
	public Class getReturnedClass() {
		return BankAccount.class;
	}
	public String getName() { 
			return "bankAccount"; 
	}
	
	public void set(PreparedStatement st, Object value, int index) throws SQLException {
		BankAccount ba = (BankAccount) value;
		st.setString(index, ba==null?null:ba.getValue());
	}

	public String objectToSQLString(Object value, Dialect dialect) throws Exception {
		BankAccount ba = (BankAccount) value;
		return '\'' + (ba==null?null:ba.getValue()) + '\'';
	}

	public String toString(Object value) {
		BankAccount ba = (BankAccount) value;
		return ba==null?null:ba.getValue();
	}

	public Object get(ResultSet rs, String name) throws SQLException {
		String account = rs.getString(name);
		if (account== null) {
			return null;
		}
		BankAccount ba = new BankAccount();
		ba.setEntity(StringUtils.substring(account,0,4));
		ba.setOffice(StringUtils.substring(account,4,8));
		ba.setControl(StringUtils.substring(account,8,10));
		ba.setAccount(StringUtils.substring(account,10));
		return ba;
	}

}
