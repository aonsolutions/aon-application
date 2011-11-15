package com.code.aon.common.dao;

import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class manages bean metadata such as alias names.
 * 
 * @author Consulting & Development. Aimar Tellitu - 29-jun-2005
 * @since 1.0
 * 
 */
public class DAOConstants {
	
	/**
	 * Obtain a suitable <code>Logger</code>.
	 */
    private final static Logger LOGGER = LoggerFactory.getLogger(DAOConstants.class);
//	private static final String RESOURCE_NAME = "/dao/constants.xml";
	private static final String RESOURCE_NAME = "/com/code/aon/entity/master/dao/constants.xml";
	private static Map<String,DAOConstantsEntry> DAO_CONSTANTS; 
	private static Set<String> CHECKED_RESOURCES;	

	/**
	 * Reset the DAO Constants Entry information.
	 */
	private static void reset() {
		DAO_CONSTANTS = new HashMap<String,DAOConstantsEntry>();
		CHECKED_RESOURCES = new HashSet<String>();
	}
	
	/**
	 * Return the DAOConstantsEntry bound to entity Class.
	 * 
	 * @param entityClass
	 * @return The DAOConstantsEntry bound to entity Class.
	 */
	public static DAOConstantsEntry getDAOConstant( Class entityClass ) {
		String entityPojo = "com.code.aon.entity.master." + entityClass.getSimpleName();
		DAOConstantsEntry entry = getDAOConstant( entityPojo );
		if (entry == null) {
			entry = getDAOConstant( entityClass.getName() );
		} else {
			entry.setPojo(entityClass.getName());
		}
		return entry;
	}

	/**
	 * Return the DAOConstantsEntry bound to POJO Class name.
	 * 
	 * @param pojo
	 * @return The DAOConstantsEntry bound to POJO Class name.
	 */
	public static DAOConstantsEntry getDAOConstant( String pojo ) {
		return DAO_CONSTANTS.get( pojo );
	}

	/**
	 * Create a DAOConstantsEntry bound to POJO Class name.
	 * 
	 * @param pojo
	 * @return The DAOConstantsEntry bound to POJO Class name.
	 */
	public static DAOConstantsEntry createDAOConstant( String pojo ) {
		String[] tokens = StringUtils.split(pojo,".");
		String entityPojo = "com.code.aon.entity.master." + tokens[tokens.length - 1];
		DAOConstantsEntry entry = getDAOConstant( entityPojo );
		if ( entry == null ) {
			//String resource = getResource( pojo );
			if (! CHECKED_RESOURCES.contains(RESOURCE_NAME) ) {			
				InputStream in = DAOConstantsEntry.class.getResourceAsStream(RESOURCE_NAME);
				if ( in != null ) {
					LOGGER.debug( RESOURCE_NAME + " found, obtaining properties" );					
					DAOConstantsReader.parse( RESOURCE_NAME, in );
					entry = DAO_CONSTANTS.get( entityPojo );
				}
				CHECKED_RESOURCES.add(RESOURCE_NAME);
			}
		}
		return entry;
	}

	/**
	 * Return the resource bound to the POJO Class name.
	 * 
	 * @param pojo
	 * @return The resource bound to the POJO Class name.
	 */
//	private static String getResource( String pojo ) {
//		StringBuffer sb = new StringBuffer( "/" );
//		int pos = pojo.lastIndexOf('.');
//		if ( pos != -1 ) {
//			sb.append( pojo.substring(0, pos).replace('.', '/') );			
//		}
//		sb.append( RESOURCE_NAME );
//		return sb.toString();
//	}

	/**
	 * Add a new bean entry.
	 * 
	 * @param entry
	 */
	protected static void addBeanEntry( DAOConstantsEntry entry ) {
		String pojo = entry.getPojo();
		String[] tokens = StringUtils.split(pojo,".");
		String entityPojo = "com.code.aon.entity.master." + tokens[tokens.length -1];
		DAO_CONSTANTS.put( entityPojo, entry );
	}
	
	static {
		reset();
	}
	
}
