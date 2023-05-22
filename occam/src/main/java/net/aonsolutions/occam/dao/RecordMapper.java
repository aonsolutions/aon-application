package net.aonsolutions.occam.dao;

import java.io.Serializable;
import java.util.function.Supplier;

import org.jooq.Record;

import net.aonsolutions.watson.client.Pair;

class RecordMapper <T extends Serializable> extends Pair<Record,T> {
	
	private static final long serialVersionUID = 2371435245238661388L;
	
	RecordMapper(Record rec, Supplier<T> supplier) {
		super(rec, supplier.get());
	}
	
	T get() {
		return getRight();
	}
	
	Record getRecord() {
		return getLeft();
	}
}
