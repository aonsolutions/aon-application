package com.code.aon.product.util;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.hibernate.dialect.Dialect;
import org.hibernate.type.StringType;

public class DiscountExpressionUserType extends StringType {

	@Override
	@SuppressWarnings("unchecked")
	public Class getReturnedClass() {
		return DiscountExpression.class;
	}
	
	@Override
	public String getName() { 
		return "discountExpression"; 
	}

	@Override
	public Object fromStringValue(String xml) {
		if (xml== null) {
			return null;
		}
		return new DiscountExpression(xml);
	}

	@Override
	public String toString(Object value) {
		if ( value == null ) {
			return null;
		}
		if (value instanceof String) {
			return (String) value;
		}
		return ((DiscountExpression) value).getDiscountExpr();
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