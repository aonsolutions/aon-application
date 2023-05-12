package net.aonsolutions.occam.api.filter;

import java.io.Serializable;

import org.jooq.Record;

public interface AonFacade extends Serializable{

	@FunctionalInterface
	public interface AonFillerBuilder<T> {
		public T build(Record rec);
	}
	
}
