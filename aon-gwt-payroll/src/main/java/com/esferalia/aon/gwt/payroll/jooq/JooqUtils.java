package com.esferalia.aon.gwt.payroll.jooq;

import java.util.Date;

public class JooqUtils {	
	
	public static <T> T get(Class<T> toType, Object... values) {

		for (Object value : values) {
			if (value != null)
				return (T) value;
		}
		return null;
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
	
	public static java.sql.Date date2sql(Date date) {
		return date != null ? new java.sql.Date(date.getTime()) : null;
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
