/**
 * 
 */
package com.code.aon.jaas.client.ast.core;

import java.util.Properties;

import com.code.aon.jaas.client.ast.INodeVisitor;
import com.code.aon.jaas.client.ast.IOption;

/**
 * @author Consulting & Development. Iñaki Ayerbe - 07/05/2007
 *
 */
public class Option implements IOption {

	/** Option name */
	private String name;

	/** Option value */
	private String value;

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.client.ast.INode#getId()
	 */
	public String getId() {
		return name;
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.client.ast.IOption#getName()
	 */
	public String getName() {
		return name;
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.client.ast.IOption#getValue()
	 */
	public String getValue() {
		return value;
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.client.ast.IOption#getProperties()
	 */
	public Properties toProperties() {
		Properties props = new Properties();
		props.setProperty( "name", this.name );
		props.setProperty( "value", this.value );
		return props;
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.client.ast.INode#accept(com.code.aon.jaas.client.ast.INodeVisitor)
	 */
	public void accept(INodeVisitor visitor) {
	}

}
