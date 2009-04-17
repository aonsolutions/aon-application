package com.code.aon.db;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.hibernate.cfg.Configuration;
import org.hibernate.mapping.ForeignKey;
import org.hibernate.mapping.PersistentClass;

public class DependencyResolver {
	
	private static final Logger LOGGER = Logger.getLogger(DependencyResolver.class.getName());
	
	private Configuration configuration;

	public DependencyResolver(Configuration configuration) {
		this.configuration = configuration;
	}
	
	@SuppressWarnings("unchecked")
	private Set<Class> getDependencies( Class entity ) {
    	PersistentClass pc = configuration.getClassMapping(entity.getName());
    	Set<Class> result = new HashSet<Class>();
    	Iterator it = pc.getTable().getForeignKeyIterator();
    	while ( it.hasNext() ) {
    		ForeignKey fk = (ForeignKey) it.next();
    		PersistentClass foreignPC = configuration.getClassMapping(fk.getReferencedEntityName());
    		result.add( foreignPC.getMappedClass() );
    	}
    	return result;
    }	
	
    private void process( List<Class<? extends Serializable>> list, Set<Class<? extends Serializable>> processed, Class<? extends Serializable> entity ) {
    	if (! processed.contains(entity) ) {
       		if (! list.contains(entity) ) {
	        	processed.add( entity );
	    		for( Class<? extends Serializable> dependency : getDependencies( entity ) ) {
               		process(list, processed, dependency );        			
	           	}
	    		list.add( entity );
	           	processed.remove( entity );
       		}	           
    	} else {
    		LOGGER.warning( "Entity is being processed: " + entity );
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
