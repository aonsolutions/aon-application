package com.code.aon.ui.form.lookup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.common.lookup.LookupUtils;

/**
 * LookupBean is the class used to implement a Lookup creating an SQL sentence which will be
 * executed to retrive the required data.
 */
public abstract class AbstractLookupBean implements ILookupBean {

	/** Obtains a suitable Logger. */
	private static final Logger LOGGER = Logger.getLogger(AbstractLookupBean.class
			.getName());
	
	/** The foreign columns. */
	private List<String> foreignJoinProperties;

	/** The properties to be displayed. */
	private List<String> foreignDisplayProperties;
	
	/** The alias prefix. */
	private String aliasPrefix;
	
    /** The ids. */
    private String ids;
    
    private Map<String,String> idsMap;
    
	/** The list of alias. */    
    private List<String> alias;

	/** The list of display alias. */    
    private List<String> displayAlias;
    
	/**
	 * The Constructor.
	 */
	public AbstractLookupBean() {
		this.foreignDisplayProperties = Collections.emptyList();
	}

	/**
	 * Sets the foreign table join properties.
	 * 
	 * @param properties the foreign table join properties
	 */
	public void setForeignJoinProperties(List<String> properties) {
		this.foreignJoinProperties = properties;
	}
	
	/**
	 * Gets the foreign table join properties.
	 * 
	 * @return the foreign table join properties
	 */
	protected List<String> getForeignJoinProperties() {
		return foreignJoinProperties;
	}

	/**
	 * Sets the foreign table display columns.
	 * 
	 * @param properties the foreign table display columns
	 */
	public void setForeignDisplayProperties(List<String> properties) {
		this.foreignDisplayProperties = properties;
	}
	
	/**
	 * Gets the foreign table display columns.
	 * 
	 * @return the display properties
	 */
	protected List<String> getForeignDisplayProperties() {
		return foreignDisplayProperties;
	}

	/**
	 * Gets the alias prefix.
	 * 
	 * @return the alias prefix
	 */
	public String getAliasPrefix() {
		return this.aliasPrefix;
	}

	/**
	 * Sets the alias prefix.
	 * 
	 * @param aliasPrefix the alias prefix
	 */
	public void setAliasPrefix(String aliasPrefix) {
		this.aliasPrefix = aliasPrefix;
	}
	
	/**
	 * Gets the alias.
	 * 
	 * @return the alias
	 */
	protected List<String> getAlias() {
		if ( this.alias == null ) {
			this.alias = new ArrayList<String>();
			for( String joinProperty : getForeignJoinProperties() ) {		
				alias.add( this.aliasPrefix + "_" + joinProperty.replace('.', '_') );
			}
			this.alias.addAll( getDisplayAlias() );
		}
		return this.alias;
	}

	/**
	 * Gets the display alias.
	 * 
	 * @return the display alias
	 */
	protected List<String> getDisplayAlias() {
		if ( this.displayAlias == null ) {
			this.displayAlias = new ArrayList<String>();
			for( String displayProperty : getForeignDisplayProperties() ) {
				displayAlias.add( this.aliasPrefix + "_" + displayProperty.replace('.', '_') );
			}
		}
		return this.displayAlias;
	}
	
	/**
	 * Gets the empty map.
	 * 
	 * @return the empty map
	 */
	protected Map<String, Object> getEmptyMap() {
		Map<String, Object> map = new HashMap<String,Object>();
		for( String displayAlias : getDisplayAlias() ) {
			map.put( displayAlias, "" );
		}
		return map;
	}
	
    /**
     * Execute reset action.
     * 
     * @param event
     */
	protected void onReset(ActionEvent event) {
        throw new NoSuchMethodError("Not implemented");		
	}

    /**
     * Execute search edition action.
     * 
     * @param event
     */
    public void onEditSearch(ActionEvent event) {
        throw new NoSuchMethodError("Not implemented");    	
    }
	
    /**
     * Init the data model for the select window.
     * 
     * @throws ManagerBeanException 
     */
	protected abstract void initSelectWindowModel() throws ManagerBeanException;
	
	/**
	 * Gets the ids.
	 * 
	 * @return the ids
	 */
	public String getIds() {
		return this.ids;
	}

	public void setIds(String ids) {
		this.ids = StringUtils.isEmpty(ids) ? null : ids;
		this.idsMap = null;
	}
	
    public Map<String, String> getIdsMap() {
    	if (idsMap == null) {
    		idsMap = LookupUtils.getIdMap( ids, getAlias() );
    	}
		return idsMap;
	}

	/**
     * Get involved parameters.
     * 
     * @return boolean
     * @throws ManagerBeanException 
     */
    public boolean getParameters() throws ManagerBeanException {
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        String idsValue = (String) ec.getRequestParameterMap().get("ids");
        String typeValue = (String) ec.getRequestParameterMap().get("type");
        boolean hasParameters = (idsValue != null) || (typeValue != null);
        if (hasParameters) {
        	setIds( idsValue );
            LOGGER.fine("ids:[" + ids + "]");
            if ( typeValue != null ) {
            	if ( "new".equals(typeValue) ) {
            		onReset( null );
            	} else if ( "search".equals(typeValue) ) {
            		onEditSearch(null);
            	} else if ( "list".equals(typeValue) ) {        
            		initSelectWindowModel();
            	} 
            }
        }
        return false;
    }

    /**
     * Set involved parameters.
     * 
     * @param value
     */
    @SuppressWarnings("unused")
    public void setParameters(boolean value) {
    	System.out.print( "Jurl: " + value );
    }
    
}