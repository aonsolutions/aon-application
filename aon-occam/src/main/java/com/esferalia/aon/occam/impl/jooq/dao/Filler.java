package com.esferalia.aon.occam.impl.jooq.dao;

import org.jooq.Field;
import org.jooq.Record;

public class Filler {
	
	protected static Boolean checkField(Record r , Field<?> f) {
		Boolean bool = false;
		for(Integer i = 0; i < r.fields().length; i++) {
			if(f.equals(r.fields()[i])) {
				bool = true;
			} 
		}
		return bool;
	}
	
	protected static <T> T getValue(Record r, Field<T> field) {
		if(checkField(r, field)) {
			return r.getValue(field);
		}
		return null;
	}
	
	protected static Boolean getBoolean(Record r, Field<Byte> field) {
		if(checkField(r, field)) {
			return r.getValue(field) == 1;
		}
		return null;
	}
}
