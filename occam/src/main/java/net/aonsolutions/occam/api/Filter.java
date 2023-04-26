package net.aonsolutions.occam.api;

import java.io.Serializable;

public interface Filter extends Serializable{
	
	public interface Property<T> {
		Filter eq(T t);
		Filter ne(T t);
		Filter le(T t);
		Filter lt(T t);
		Filter gt(T t);
		Filter ge(T t);
		Filter in(T[] t);
		Filter notIn(T[] t);
		Filter isNull();
		Filter isNotNull();
		Filter like(T t);
		Filter between(T min, T max);
	}
	public Filter or(Filter filter);
	public Filter and(Filter filter);
	public Filter not(Filter filter);
	
}
