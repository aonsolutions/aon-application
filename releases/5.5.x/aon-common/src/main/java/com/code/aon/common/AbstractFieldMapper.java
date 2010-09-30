package com.code.aon.common;

import java.util.Hashtable;
import java.util.Map;

import com.code.aon.common.dao.sql.DAOException;

/**
 * Abstract Base Class for all DAO classes with a mapping between column alias and 
 * database column names.
 * 
 * @author Consulting & Development. Aimar Tellitu - 01-jun-2005
 * @since 1.0
 *  
 */
public abstract class AbstractFieldMapper {

    /**
     * Return a <code>Map</code> with column alias and column names. 
     * 
     * @param alias String[] column alias. 
     * @param names String[] column names.
     * @return Map
     */
    public static Map<String,String> getMap( String[] alias, String[] names ) {
		Map<String,String> map = new Hashtable<String,String>();
    	if ( alias != null ) {
    		for( int i = 0; i < alias.length; i++ ) {
    			map.put( alias[i], names[i] );
    		}
    	}
		return map;
    }
    
    /**
     * Return the column field name.
	 * 
	 * @param alias String
	 * @return String
	 * @throws DAOException
	 */
	public String getFieldName(String alias) throws DAOException {
		String fieldName = (String) getFieldMap().get( alias);
		if ( fieldName == null ) {
			throw new DAOException("No Field match for this alias: " + alias);
		}
		return fieldName;
	}
	
	/**
	 * Return a <code>Map</code> in order to resolve Transfer Object property names bound to
	 * alias name.
	 * 
	 * @return Map.
	 */
	protected abstract Map getFieldMap();

}
