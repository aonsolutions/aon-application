package com.code.aon.db;

import java.io.Serializable;
import java.util.logging.Logger;

import org.apache.commons.lang.ObjectUtils;
import org.hibernate.ReplicationMode;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.type.AssociationType;
import org.hibernate.type.Type;

public class OnTheFlyReplicator implements IEntityManager {

	private static final Logger LOGGER = Logger.getLogger(OnTheFlyReplicator.class.getName());
	
	private HibernateDataManager hdm;
	
	private QueryIterable<Object> entityIterable;
	
	private Session session;
	
	private Transaction tx;
	
	private boolean insert;
	
	private boolean onDemand;
	
	public OnTheFlyReplicator( HibernateDataManager hdm, QueryIterable<Object> entityIterable, boolean insert ) {
		this.hdm = hdm;
		this.entityIterable = entityIterable;
		this.insert = insert;
	}

	public boolean isOnDemand() {
		return onDemand;
	}

	public void setOnDemand(boolean onDemand) {
		this.onDemand = onDemand;
	}

	private boolean insertOnDemand( String entityName, Object object, boolean replicate ) {
    	ClassMetadata cm = entityIterable.getSessionFactory().getClassMetadata(entityName);
		Serializable id = cm.getIdentifier(object, session.getEntityMode());
		Object _object = session.get(entityName, id);
		if ( _object == null ) {
	    	String[] names = cm.getPropertyNames();
	    	Type[] types = cm.getPropertyTypes();
	    	for( int i = 0; i < names.length; i++ ) {
	    		if ( types[i].isEntityType() || types[i].isAnyType() ) {
	    			Object value = cm.getPropertyValue(object, names[i], entityIterable.getSession().getEntityMode());
	    			if ( value != null ) {
	    				AssociationType at = (AssociationType) types[i];
	    				String propertyEntityName = at.getAssociatedEntityName( (SessionFactoryImplementor) entityIterable.getSessionFactory() );
    					insertOnDemand( propertyEntityName, value, true);
	    			}
	    		}
	    	}
			if ( replicate ) {
				session.replicate( entityName, object, ReplicationMode.EXCEPTION );
			}	
			return false;
		}
		return true; 
	}
	
	private void initTransaction() {
		session = hdm.getImportFactory().openSession();
		tx = session.beginTransaction();
	}

	private void endTransaction() {
		tx.commit();
		session.close();
	}
	
	private boolean isEntityFullImported(Class entity) {
		Integer rowCount = entityIterable.getRowCount();
		Integer rowCount2 = CountComparator.getRowCount(hdm.getImportFactory(), entity);
		LOGGER.info( "Count entity " + entity.getName() + ": " + rowCount + "/" + rowCount2 );
		return ObjectUtils.equals(rowCount, rowCount2);
	}
	
	public void proccess(Class entity) throws EntityProcessException {
		String entityName = entity.getName();
		LOGGER.info( "Imported of entity " + entityName + " started" );
		this.entityIterable.setEntity( entity );
        int counter = 0;
        int imported = 0;
        initTransaction();
        if (! isEntityFullImported(entity) ) {
	        for( Object element : entityIterable ) {
	        	boolean exists = false;
	        	if ( onDemand ) {
	        		exists = insertOnDemand( entityName, element, false );
	        	}
	        	if (! exists ) {
		        	entityIterable.getSession().evict(element);
		        	if ( insert ) {
		        		session.save( entityName, element );        		
		        	} else {
		        		session.replicate( entityName, element, ReplicationMode.EXCEPTION );        		
		        	}
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
        } else {
            LOGGER.info( "Imported of " + entityName + " skipped" );
        }
        endTransaction();
	}
	
}
