package com.code.aon.db;

public interface IEntityManager {

	void proccess( Class entity ) throws EntityProcessException; 
	
}
