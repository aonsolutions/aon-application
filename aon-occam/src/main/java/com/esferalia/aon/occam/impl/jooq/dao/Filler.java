package com.esferalia.aon.occam.impl.jooq.dao;

import org.jooq.Field;
import org.jooq.Record;

public class Filler {
	
	protected static boolean checkField(Record r , Field<?> f) {
		Boolean bool = false;
		for(Integer i = 0; i < r.fields().length; i++) {
			if(f.equals(r.fields()[i])) {
				bool = true;
			} 
		}
		return bool;
	}
	
	protected static <T> boolean isNull(Record r, Field<T> field) {
		return getValue(r, field) == null;
	}

	protected static boolean hasValue(Record r, Field<?> field) {
		return checkField(r, field) && r.getValue(field) != null;
	}

	protected static <T> T getValue(Record r, Field<T> field) {
		return checkField(r, field)
			? r.getValue(field)
			: null;
	}

	protected static String getString(Record r, Field<String> field) {
		return checkField(r, field) && r.getValue(field) != null
			? r.getValue(field)
			: "";
	}
	
	protected static int getInteger(Record r, Field<Integer> field) {
		return checkField(r, field) && r.getValue(field) != null
			? r.getValue(field)
			: 0;
	}
	
	protected static short getShort(Record r, Field<Short> field) {
		return checkField(r, field) && r.getValue(field) != null
			? r.getValue(field) 
			: 0;
	}
	
	protected static double getDouble(Record r, Field<Double> field) {
		return checkField(r, field) && r.getValue(field) != null
			? r.getValue(field) 
			: 0.0;
	}
	
	protected static boolean getBoolean(Record r, Field<Byte> field) {
		if(checkField(r, field) && r.getValue(field) != null) {
			return r.getValue(field) == 1;
		}
		return false;
	}
	
	protected static byte getByte(Record r, Field<Byte> field) {
		if(checkField(r, field) && r.getValue(field) != null) {
			return r.getValue(field);
		}
		return 0;
	}
	
	protected static <K> K getEnum(Record r, Field<Byte> field, Class<K> keyType) {
		if ( getValue(r, field) == null ) return null;
		byte n = getByte(r, field);
		if ( n < 0 ) return null;
		K[] values  = keyType.getEnumConstants();
		if ( n >= values.length ) return null;
		return values[n];
	}
	
}
