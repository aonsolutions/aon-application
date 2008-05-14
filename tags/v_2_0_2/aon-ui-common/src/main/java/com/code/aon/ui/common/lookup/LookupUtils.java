/*
 * Created on 01-jul-2005
 *
 */
package com.code.aon.ui.common.lookup;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringEscapeUtils;

/**
 * LookupUtils includes common methods used by classes related with the Lookup. 
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 01-jul-2005
 * @since 1.0
 */

public class LookupUtils {

	/**
	 * Return ids as an array.
	 * 
	 * @param aliasSet the alias set
	 * @param ids String whith the ids
	 * 
	 * @return the ids as an array of Strings
	 */
	public static Map<String,String> getIdMap( String ids, Collection<String> aliasSet ) {
		if ( (ids == null) || (ids.trim().length() == 0) ) {
			return Collections.emptyMap();
		}
		StringTokenizer st = new StringTokenizer( ids, " ," );
		Map<String,String> idMap = new HashMap<String, String>();
		for( int i = 0; st.hasMoreTokens(); i++ ) {
			String token = st.nextToken();
			String componentId = null;
			String alias = null;
			int pos = token.indexOf('|');
			if ( pos != -1 ) {
				componentId = token.substring(pos+1);
				alias = token.substring(0, pos);
			} else {
				componentId = token;
				for( String value : aliasSet ) {
					if ( componentId.indexOf(value) != -1 ) {
						alias = value;
						break;
					}
				}
			}
			idMap.put( alias, componentId );
		}
		return idMap;
	}
	
	/**
	 * Checks if the id is a substring of the alias
	 * 
	 * @param ids the ids
	 * @param alias the alias
	 * 
	 * @return the string
	 */
	private static String getComponentId( Map<String,String> idMap, String alias ) {
		String result = idMap.get(alias);
		return (result != null) ? result : alias;
	}
	
    /**
     * Creates the xml that will be sent in the response
     * 
     * @param map the map
     * @param idMap the ids Map
     * 
     * @return the response XML
     */
    public static final String getResponseXML(Map<String,Object> map, Map<String,String> idMap) {
        StringBuffer sb = new StringBuffer("<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?><item>");
        for( Map.Entry<String,Object> entry : map.entrySet() ) {
            String componentId = getComponentId( idMap, entry.getKey() );
            sb.append( "<entry name=\"");
            sb.append( StringEscapeUtils.escapeXml(componentId) );
            sb.append( "\" value=\"");
            String value = ( entry.getValue() == null ) ? "" : entry.getValue().toString();
            sb.append( StringEscapeUtils.escapeXml(value) );
            sb.append( "\" />");
        }
        sb.append("</item>");
        return sb.toString();
    }

    /**
     * Creates the xml that will be sent in the response
     * 
     * @param ids the ids
     * @param map the map
     * 
     * @return the response XML
     */
    public static final String getResponseXML(Map<String,Object> map, String ids) {
        Map<String,String> idMap = getIdMap( ids, map.keySet() );
        return getResponseXML(map, idMap);
    }

}