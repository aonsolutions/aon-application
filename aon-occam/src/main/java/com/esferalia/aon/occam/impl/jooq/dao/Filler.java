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
	
	protected static <T> T getValue(Record r, Field<T> field) {
		return checkField(r, field)
			? r.getValue(field)
			: null;
	}

	protected static int getInteger(Record r, Field<Integer> field) {
		return checkField(r, field) 
			? r.getValue(field)
			: 0;
	}
	
	protected static double getDouble(Record r, Field<Double> field) {
		return checkField(r, field) 
			? r.getValue(field)
			: 0.0;
	}
	
	protected static boolean getBoolean(Record r, Field<Byte> field) {
		if(checkField(r, field)) {
			return r.getValue(field) == 1;
		}
		return false;
	}
}
