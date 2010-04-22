package com.code.aon.db;

public interface IEntityManager<E> {

	void proccess( Class<E> entity ) throws EntityProcessException; 
	
}
