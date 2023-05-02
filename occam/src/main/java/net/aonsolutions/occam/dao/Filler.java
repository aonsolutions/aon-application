package net.aonsolutions.occam.dao;

import java.util.function.Supplier;

import org.jooq.Field;
import org.jooq.Record;

abstract class Filler<T> {
	
	abstract T map(Record r,Supplier<T> s);
	
	protected boolean checkField(Record r , Field<?> f) {
		Boolean bool = false;
		for(Integer i = 0; i < r.fields().length; i++) {
			if(f.equals(r.fields()[i])) {
				bool = true;
			} 
		}
		return bool;
	}
	
	protected <K> K getValue(Record r, Field<K> field) {
		return checkField(r, field)
			? r.getValue(field)
			: null;
	}

	protected boolean getBoolean(Record r, Field<Byte> field) {
		if(checkField(r, field) && r.getValue(field) != null) {
			return r.getValue(field) == 1;
		}
		return false;
	}
	
}
