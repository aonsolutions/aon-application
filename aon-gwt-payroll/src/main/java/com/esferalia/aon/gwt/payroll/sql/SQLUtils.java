package com.esferalia.aon.gwt.payroll.sql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;

import com.esferalia.aon.gwt.payroll.shared.Deduction;
import com.esferalia.aon.gwt.payroll.shared.Payment;
import com.esferalia.aon.gwt.payroll.shared.Salary;

public class SQLUtils {

	public static void setDate(PreparedStatement stmt, int parameterIndex,
			Date x) throws SQLException {
		SQLUtils.set(stmt, parameterIndex, x, Types.DATE);
	}

	public static void setInt(PreparedStatement stmt, int parameterIndex,
			Integer x) throws SQLException {
		SQLUtils.set(stmt, parameterIndex, x, Types.INTEGER);
	}

	public static void setString(PreparedStatement stmt, int parameterIndex,
			String x) throws SQLException {
		SQLUtils.set(stmt, parameterIndex, x, Types.VARCHAR);
	}

	public static void setShort(PreparedStatement stmt, int parameterIndex,
			Short x) throws SQLException {
		SQLUtils.set(stmt, parameterIndex, x, Types.SMALLINT);
	}

	public static void set(PreparedStatement stmt, int parameterIndex,
			Object x, int targetSqlType) throws SQLException {
		if (x != null)
			stmt.setObject(parameterIndex, x, targetSqlType);
		else
			stmt.setNull(parameterIndex, targetSqlType);
	}
	
	public static <T> T get(ResultSet rs, Class<T> toType, String ...columnLabels) throws SQLException {
		for (String columnLabel : columnLabels) {
			Object object = rs.getObject(columnLabel);
			if ( object != null ) 
				return (T)object;
		}
		return null;
	}


	public static Date getDate (ResultSet rs, String ...columnLabels) throws SQLException {
		return get(rs, Date.class, columnLabels);
	}

	public static Short getShort (ResultSet rs, String ...columnLabels) throws SQLException {
		return get(rs, Short.class, columnLabels);
	}

	public static String getString (ResultSet rs, String ...columnLabels) throws SQLException {
		return get(rs, String.class, columnLabels);
	}
	
	public static Payment.Type getPaymentType (ResultSet rs, String ...columnLabels) throws SQLException {
		return getPaymentType(get(rs, Object.class, columnLabels));
	}
	
	public static Salary.Type getSalaryType (ResultSet rs, String ...columnLabels) throws SQLException {
		return getType(get(rs, Object.class, columnLabels), Salary.Type.class);
	}

	public static Integer getInteger (ResultSet rs, String ...columnLabels) throws SQLException {
		return get(rs, Integer.class, columnLabels);
	}

	public static boolean sameString(String s1, String s2) {
		if (s1 == s2)
			return true;
		if (s1 == null)
			return false;
		if (s2 == null)
			return false;
	
		return s1.trim().equals(s2.trim());
	}

	public static Payment.Type getPaymentType(Object object) {
		return getType(object, Payment.Type.class);
	}

	public static java.sql.Date date2sql(Date date) {
		return date != null ? new java.sql.Date(date.getTime()) : null;
	}

	public static Short enum2Short(Enum<?> type) {
		return type == null ? null : (short) type.ordinal();
	}

	public static Deduction.Type getDeductionType(Object object) {
		return getType(object, Deduction.Type.class);
	}

	public static <T extends Enum<?>> T getType(Object object, Class<T> type) {
		if (object == null)
			return null;
		if (!(object instanceof Number))
			return null;
		int ordinal = ((Number) object).intValue();
		if (ordinal < 0)
			return null;
		T constants[] = type.getEnumConstants();
		if (ordinal >= constants.length)
			return null;
	
		return constants[ordinal];
	}
	

}
