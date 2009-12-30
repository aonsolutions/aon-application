package com.code.aon.ui.form.lookup;

import java.util.List;
import java.util.Map;

import javax.faces.model.DataModel;

import com.code.aon.common.ManagerBeanException;

/**
 * LookupBean is the class used to implement a Lookup creating an SQL sentence which will be
 * executed to retrive the required data
 */
@Deprecated
public interface ILookupBean {

	/**
	 * Sets the foreign column join properties.
	 * 
	 * @param properties the foreign column join properties
	 */
	void setForeignJoinProperties(List<String> properties);
	

	/**
	 * Sets the foreign table display properties.
	 * 
	 * @param properties the foreign table display properties
	 */
	void setForeignDisplayProperties(List<String> properties);

	/**
	 * Sets the alias prefix.
	 * 
	 * @param aliasPrefix the alias prefix
	 */
	void setAliasPrefix(String aliasPrefix);
	
	/**
	 * Gets the lookups.
	 * 
	 * @param values the values
	 * 
	 * @return the lookups
	 */
	Map<String, Object> getLookups( Object[] values );

	/**
	 * Gets the model.
	 * 
	 * @return the model
	 * @throws ManagerBeanException 
	 */
    DataModel getModel() throws ManagerBeanException;
    
	/**
	 * Gets the lookups as XML.
	 * 
	 * @return the lookups as XML
	 * @throws ManagerBeanException 
	 */
	String getLookupsAsXML() throws ManagerBeanException;
	
	/**
	 * Sets the ids.
	 * 
	 * @param ids the ids
	 */
	void setIds(String ids);
		

   /**
     * Gets the ids map.
     * 
     * @return the ids map
     */
    Map<String, String> getIdsMap();

}