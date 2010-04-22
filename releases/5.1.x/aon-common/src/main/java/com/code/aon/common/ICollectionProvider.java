package com.code.aon.common;

import java.util.Collection;

/**
 * Interface that provides a collection of objects.
 * 
 * @author Consulting & Development.
 * 
 */

public interface ICollectionProvider {

	/**
	 * Return the collection.
	 * 
	 * @return The collection.
	 */
	Collection getCollection();

	Collection getCollection(boolean forceRefresh) throws ManagerBeanException;
}