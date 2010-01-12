package com.code.aon.db;

import java.util.logging.Logger;

import org.hibernate.ReplicationMode;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class OnTheFlyReplicator implements IEntityManager {

	private static final Logger LOGGER = Logger.getLogger(OnTheFlyReplicator.class.getName());
	
	private HibernateDataManager hdm;
	
	private QueryIterable<Object> entityIterable;
	
	private Session session;
	
	private Transaction tx;
	
	private boolean insert;
	
	public OnTheFlyReplicator( HibernateDataManager hdm, QueryIterable<Object> entityIterable, boolean insert ) {
		this.hdm = hdm;
		this.entityIterable = entityIterable;
		this.insert = insert;
	}

	private void initTransaction() {
		session = hdm.getImportFactory().openSession();
		tx = session.beginTransaction();
	}

	private void endTransaction() {
		tx.commit();
		session.close();
	}
	
	public void proccess(Class entity) throws EntityProcessException {
		String entityName = entity.getName();
		LOGGER.info( "Imported of entity " + entityName + " started" );
		this.entityIterable.setEntity( entity );
        int counter = 0;
        int imported = 0;
        initTransaction();
        for( Object element : entityIterable ) {
        	entityIterable.getSession().evict(element);
        	if ( insert ) {
        		session.save( entityName, element );        		
        	} else {
        		session.replicate( entityName, element, ReplicationMode.EXCEPTION );        		
        	}
        	if ( (hdm.getMaxImport() != 0) && (++imported == hdm.getMaxImport()) ) {
        		LOGGER.info( "Imported " + counter + " elements of " + entityName );
				endTransaction();
        		initTransaction();
        		imported = 0;
        	}
        	counter++;
        }
        LOGGER.info( "TOTAL Imported elements of " + entityName + ": " + counter );
        endTransaction();
	}
	
}
