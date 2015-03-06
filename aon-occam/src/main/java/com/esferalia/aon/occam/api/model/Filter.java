package com.esferalia.aon.occam.api.model;


public interface Filter {
	
	public interface Property<T> {
		Filter eq(T t);
		Filter ne(T t);
		Filter le(T t);
		Filter lt(T t);
		Filter gt(T t);
		Filter ge(T t);
		Filter in(T[] t);
		Filter isNull();
		Filter isNotNull();
	}
	
	
	public Filter or(Filter filter);
	public Filter and(Filter filter);
	public Filter not(Filter filter);
	
	
}
