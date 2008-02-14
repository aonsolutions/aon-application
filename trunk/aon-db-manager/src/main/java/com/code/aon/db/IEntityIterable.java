package com.code.aon.db;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

public interface IEntityIterable<E> extends Iterable<E> {

	void setSessionFactory( SessionFactory sessionFactory );
	
	void setMaxResults( int maxResults );	
	
	void setEntity( Class entity );
	
	void setAsElement( boolean asElement );
	
	void flushed();
	
	Session getSession();
	
}
