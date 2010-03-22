package com.code.aon.db;

public interface IEntityGenerator<E> extends Iterable<E> {

	void setMaxResults( int maxResults );
	
	E nextObject();
	
}
