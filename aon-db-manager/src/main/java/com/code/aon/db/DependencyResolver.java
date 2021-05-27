package com.code.aon.db;

import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.hibernate.SessionFactory;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.type.AssociationType;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DependencyResolver {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DependencyResolver.class.getName());
	
	private SessionFactory sessionFactory;

	public DependencyResolver(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	@SuppressWarnings("unchecked")
	private Set<Class<? extends Serializable>> getDependencies( Class entity ) {
    	ClassMetadata cm = sessionFactory.getClassMetadata(entity);
    	Set<Class<? extends Serializable>> result = new HashSet<Class<? extends Serializable>>();
    	for( Type type : cm.getPropertyTypes() ) {
    		if ( type.isEntityType() || type.isAnyType() ) {
    			AssociationType at = (AssociationType) type;
    			Class _class = at.getReturnedClass();
    			result.add(_class);
    		}
    	}
    	return result;
    }	
	
    private void process( List<Class<? extends Serializable>> list, Set<Class<? extends Serializable>> processed, Class<? extends Serializable> entity ) {
    	if (! processed.contains(entity) ) {
       		if (! list.contains(entity) ) {
	        	processed.add( entity );
	        	Set<Class<? extends Serializable>> dependencies = getDependencies( entity );
	    		for( Class<? extends Serializable> dependency : dependencies ) {
               		process(list, processed, dependency );        			
	           	}
	    		if ( dependencies.isEmpty() ) {
	    			list.add( 0, entity );
	    		} else {
	    			list.add( entity );
	    		}
	           	processed.remove( entity );
       		}	           
    	} else {
    		LOGGER.warn( "Entity is being processed: " + entity );
    	}
    }
	
	public List<Class<? extends Serializable>> organize( List<Class<? extends Serializable>> entities ) {
		List<Class<? extends Serializable>> list  = new LinkedList<Class<? extends Serializable>>();
		Set<Class<? extends Serializable>> processed = new HashSet<Class<? extends Serializable>>();
    	for( Class<? extends Serializable> entity : entities ) {
    		process(list, processed, entity);
    	}
		return list;
	}

}
