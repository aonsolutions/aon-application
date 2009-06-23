package com.code.aon.common.dao;

import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.code.aon.common.dao.hibernate.HibernateUtil;

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
    private static final Logger LOGGER = Logger.getLogger(DAOConstants.class.getName());
	private static final String RESOURCE_NAME = "/dao/constants.xml";
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
		return getDAOConstant( entityClass.getName() );
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
		DAOConstantsEntry entry = getDAOConstant( pojo );
		if ( entry == null ) {
			String resource = getResource( pojo );
			if (! CHECKED_RESOURCES.contains(resource) ) {			
				InputStream in = DAOConstantsEntry.class.getResourceAsStream(resource);
				if ( in != null ) {
					LOGGER.fine( resource + " found, obtaining properties" );					
					DAOConstantsReader.parse( resource, in );
					entry = DAO_CONSTANTS.get( pojo );
				}
				CHECKED_RESOURCES.add(resource);
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
	private static String getResource( String pojo ) {
		StringBuffer sb = new StringBuffer( "/" );
		int pos = pojo.lastIndexOf('.');
		if ( pos != -1 ) {
			sb.append( pojo.substring(0, pos).replace('.', '/') );			
		}
		sb.append( RESOURCE_NAME );
		return sb.toString();
	}

	/**
	 * Add a new bean entry.
	 * 
	 * @param entry
	 */
	protected static void addBeanEntry( DAOConstantsEntry entry ) {
		DAO_CONSTANTS.put( entry.getPojo(), entry );
	}
	
	static {
		reset();
	}
	
}
