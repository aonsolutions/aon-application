/**
 * 
 */
package com.code.aon.jaas.client.ast.core;

import java.util.Properties;

import com.code.aon.jaas.client.ast.IDataSourceMetaData;
import com.code.aon.jaas.client.ast.INodeVisitor;

/**
 * The DataSource metadata.
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 20-jul-2006
 * @since 1.0
 *
 */

public class DataSourceMetaData implements IDataSourceMetaData {

	/**
	 * Determines if a de-serialized file is compatible with this class.
	 *
	 * Maintainers must change this value if and only if the new version
	 * of this class is not compatible with old versions. See Sun docs
	 * for details.
	 *
	 * Not necessary to include in first version of the class, but
	 * included here as a reminder of its importance.
	 */
	private static final long serialVersionUID = 6297568669113344738L;

	/** 
	 * DataSource properties. Wraps each property in a <code>Properties</code> class. 
	 */
	private Properties props = new Properties();

	/**
	 * Assign DataSource <code>URL</code>.
	 * 
	 * @param value
	 */
    public void setConnectionURL(String value) {
    	this.props.put( IDataSourceMetaData.URL, value );
    }

	/**
	 * Assign DataSource <code>DriverClass</code> .
	 * 
	 * @param value
	 */
    public void setDriverClass(String value) {
    	this.props.put( IDataSourceMetaData.DRIVER_CLASS, value );
    }

	/**
	 * Assign DataSource <code>Username</code> .
	 * 
	 * @param value
	 */
    public void setUsername(String value) {
    	this.props.put( IDataSourceMetaData.USER, value );
    }

	/**
	 * Assign DataSource <code>Password</code> .
	 * 
	 * @param value
	 */
    public void setPassword(String value) {
    	this.props.put( IDataSourceMetaData.PASSWORD, value );
    }

    /* (non-Javadoc)
	 * @see com.code.aon.jaas.client.ast.INode#getId()
	 */
	public String getId() {
		return DataSourceMetaData.class.getName();
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.client.ast.INode#accept(com.code.aon.jaas.client.ast.INodeVisitor)
	 */
	public void accept(INodeVisitor visitor) {
		visitor.visitDataSourceMetaData(this);
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.client.ast.IDataSourceMetaData#getConnectionURL()
	 */
	public String getConnectionURL() {
		return props.getProperty( IDataSourceMetaData.URL );
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.client.ast.IDataSourceMetaData#getDriverClass()
	 */
	public String getDriverClass() {
		return props.getProperty( IDataSourceMetaData.DRIVER_CLASS );
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.client.ast.IDataSourceMetaData#getUsername()
	 */
	public String getUsername() {
		return props.getProperty( IDataSourceMetaData.USER );
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.client.ast.IDataSourceMetaData#getPassword()
	 */
	public String getPassword() {
		return props.getProperty( IDataSourceMetaData.PASSWORD );
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.client.ast.IDataSourceMetaData#getProperties()
	 */
	public Properties getProperties() {
		return this.props;
	}

}
