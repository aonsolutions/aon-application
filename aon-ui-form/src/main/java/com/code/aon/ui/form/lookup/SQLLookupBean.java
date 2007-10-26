package com.code.aon.ui.form.lookup;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import org.apache.commons.beanutils.PropertyUtils;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.common.lookup.LookupUtils;
import com.code.aon.ui.form.event.IControllerListener;
import com.code.aon.ui.form.listener.LookupBeanListener;
import com.code.aon.ui.util.AonUtil;

/**
 * LookupBean is the class used to implement a Lookup creating an SQL sentence which will be
 * executed to retrive the required data
 */
public class SQLLookupBean extends AbstractLookupBean {

	/** Obtains a suitable Logger. */
	private static final Logger LOGGER = Logger.getLogger(SQLLookupBean.class
			.getName());
	
	/** The table from the lookup values will ber retrieved. */
	private String foreignTable;
	
	/** The join properties. */
	private List<String> sourceJoinProperties;
	
	/** The values. */
	private Map<String,Object> values;

	/** The controller listener. */
	private IControllerListener controllerListener;
	
	/** The resolver. */
	private ResolverFakeMap resolver;	
	
	/** The model. */
	private ListDataModel model;
	
	/**
	 * The empty constructor.
	 */
	public SQLLookupBean() {
		this.values = new HashMap<String,Object>();
		this.controllerListener = new LookupBeanListener(this);
		this.resolver = new ResolverFakeMap();		
	}
	
	/**
	 * Sets the table.
	 * 
	 * @param table the table
	 */
	public void setForeignTable(String table) {
		this.foreignTable = table;
	}
	
	/**
	 * Gets the values.
	 * 
	 * @return the values
	 */
	public Map<String, Object> getValues() {
		return values;
	}

	/**
	 * Sets the values.
	 * 
	 * @param values the values
	 */
	public void setValues(Map<String, Object> values) {
		this.values = values;
	}

	/**
	 * Gets the controller listener.
	 * 
	 * @return the controller listener
	 */
	public IControllerListener getControllerListener() {
		return controllerListener;
	}
	
	/**
	 * Sets the source table join properties.
	 * 
	 * @param joinProperties the source table join properties
	 */
	public void setSourceJoinProperties(List<String> joinProperties) {
		this.sourceJoinProperties = joinProperties;
	}

	/**
	 * Gets the source table join properties.
	 * 
	 * @return the source table join properties.
	 */
	protected List<String> getSourceJoinProperties() {
		return sourceJoinProperties;
	}

	/**
	 * Creates the sql sentence that will be executed to retrieve the data specified by:
	 * <pre>
	 *  - table
	 *  - foreignColumns
	 *  - joinProperties
	 *  - displayColumns
	 * </pre>
	 * 
	 * @param values the values
	 * 
	 * @return the sql
	 */
	private String getSql( Object[] values ) {
		StringBuffer sb = new StringBuffer();
		sb.append( "SELECT " );
		for( int i = 0; i < getForeignJoinProperties().size(); i++ ) {
			sb.append( getForeignJoinProperties().get(i) );
			if ( i+1 < getForeignJoinProperties().size() ) {
				sb.append( ',' );
			}
		}
		for( String column : getForeignDisplayProperties() ) {
			sb.append( ", " );
			sb.append( column );
		}
		sb.append( " FROM " );
		sb.append( this.foreignTable );
		if ( values != null ) {
			int size = Math.min( values.length, getForeignJoinProperties().size() );
			sb.append( " WHERE " );
			for( int i = 0; i < size; i++ ) {
				sb.append( getForeignJoinProperties().get(i) );
				sb.append( " = ?" );
				if ( i+1 < size ) {
					sb.append( " AND " );
				}
			}
		}
		return sb.toString();
	}
	
	/**
	 * Gets the lookups.
	 * 
	 * @param values the values
	 * 
	 * @return the lookups
	 */
	public Map<String, Object> getLookups( Object[] values ) {
		List<Map<String, Object>> result = resolveLookups( values );
		if ( (result != null) && (!result.isEmpty()) ) {
			if ( result.size() > 1 ) {
				LOGGER.warning( "More than 1 value retrieved for values " + values );
			} else {
				LOGGER.warning( "No results found (size=0) for values " + values );
				return result.get(0);	
			}
		} else {
			LOGGER.warning( "No results found (result=null) for values " + values );
		}
		return getEmptyMap();
	}

	/**
	 * Executes the sql sentence created by <code>getSql</code>
	 * 
	 * @param values the values
	 * 
	 * @return the list< map< string, object>>
	 */
	protected List<Map<String, Object>> resolveLookups( Object[] values ) {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		
		Connection connection = AonUtil.getSQLConnection();

		String sql = getSql(values);
		LOGGER.info( "Executing sql: " + sql );
		try {
			PreparedStatement select = connection.prepareStatement(sql);
			if ( values != null ) {
				for( int i = 0; i < values.length; i++ ) {
					select.setObject( i+1, values[i] );					
				}
			}
			List<String> alias = getAlias();		
			ResultSet rs = select.executeQuery();
			while ( rs.next() ) {
				Map<String, Object> map = new HashMap<String,Object>();
				for( int i = 0; i < alias.size(); i++ ) {
					map.put( alias.get(i), rs.getObject(i+1) );
				}
				result.add( map );
			}
			rs.close();
			select.close();
			connection.close();
		} catch ( SQLException e ) {
			LOGGER.log( Level.SEVERE, "Error executing lookup", e );			
		}
		
		return result;
	}
	
	@Override
	protected void initSelectWindowModel() throws ManagerBeanException {
		List<Map<String, Object>> list = resolveLookups( null );
		this.model = new ListDataModel(list);
	}

	/**
	 * Gets the model.
	 * 
	 * @return the model
	 * @throws ManagerBeanException 
	 */
	public DataModel getModel() throws ManagerBeanException {
		if ( model == null ) {
			initSelectWindowModel();
		}
		return model;
	}
    
	/**
	 * Gets the lookups as XML.
	 * 
	 * @return the lookups as XML
	 */
	@SuppressWarnings("unchecked")
	public String getLookupsAsXML() {
		int selectedIndex = this.model.getRowIndex();
		List<Map<String, Object>> list = (List<Map<String, Object>>) this.model.getWrappedData();
		Map<String, Object> map = list.get(selectedIndex);
		return LookupUtils.getResponseXML( map, getIdsMap() );
	}
	
	/**
	 * Gets the current map.
	 * 
	 * @param to the to
	 * 
	 * @return the current map
	 * 
	 * @throws IllegalAccessException the illegal access exception
	 * @throws NoSuchMethodException the no such method exception
	 * @throws InvocationTargetException the invocation target exception
	 */
	public Map<String,Object> getCurrentMap( ITransferObject to ) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		if ( to != null ) {
			Object[] values = new Object[getSourceJoinProperties().size()];
			for( int i = 0; i < getSourceJoinProperties().size(); i++ ) {
				try {
					values[i] = PropertyUtils.getProperty( to, getSourceJoinProperties().get(i) );
				} catch ( IllegalArgumentException iae ) {
					LOGGER.warning( "Error getting property " + getSourceJoinProperties().get(i) + " of " + to  );
				}
			}
			if (! isNull(values) ) {
				Map<String,Object> map = getLookups(values);
				return map;
			}
		}
		return null;
	}
	
	/**
	 * Reset values.
	 */
	public void resetValues() {
		for( Map.Entry<String,Object> entry : this.values.entrySet() ) {
			entry.setValue( null );
		}
	}
	
	/**
	 * Checks if is null.
	 * 
	 * @param values the values
	 * 
	 * @return true, if is null
	 */
	private boolean isNull( Object[] values ) {
		for( Object object : values ) {
			if ( object == null ) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Gets the current to.
	 * 
	 * @return the current to
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 */
	protected ITransferObject getCurrentTo() throws ManagerBeanException {
		DataModel model = this.controllerListener.getController().getModel();
		if ( model.isRowAvailable() ) {
			return (ITransferObject) model.getRowData();
		}
		return null;
	}

	/**
	 * Gets the resolver.
	 * 
	 * @return the resolver
	 */
	public Map getResolver() {
		return this.resolver;
	}

	/**
	 * ResolverFakeMap retrieves the objects using the current map.
	 */
	private class ResolverFakeMap extends HashMap {

		/**
		 * the object with the specified key in the current map.
		 * 
		 * @param key the key
		 * 
		 * @return the object
		 * 
		 * @see java.util.HashMap#get(java.lang.Object)
		 */
		@Override
		public Object get(Object key) {
			try {
				ITransferObject to = getCurrentTo();
				Map<String,Object> map = getCurrentMap( to );
				if ( map != null ) {
					return map.get(key);
				}
			} catch (IllegalAccessException e) {
				LOGGER.severe(e.getMessage());
			} catch (InvocationTargetException e) {
				LOGGER.severe(e.getMessage());
			} catch (NoSuchMethodException e) {
				LOGGER.severe(e.getMessage());
			} catch (ManagerBeanException e) {
				LOGGER.severe(e.getMessage());
			}
			return null;
		}
	}	

}
