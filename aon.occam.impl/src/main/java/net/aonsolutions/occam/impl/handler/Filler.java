package net.aonsolutions.occam.impl.handler;

import java.util.function.Function;

import org.jooq.Field;
import org.jooq.Record;

abstract class Filler<T> implements Function<Record, T> {
	Filler() {
		
	}
	
	protected static boolean isNull(Record r , Field<?> f) {
		return checkField(r,f) && getValue(r, f) == null; 
	}
	protected static boolean isNotNull(Record r , Field<?> f) {
		return !isNull(r ,f);
	}
	
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

	protected static double getDouble(Record r, Field<Double> field) {
		return checkField(r, field) && r.getValue(field) != null
			? r.getValue(field) 
			: 0.0;
	}
	
}

