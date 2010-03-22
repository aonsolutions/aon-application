package com.code.aon.common.dao;

import org.hibernate.type.Type;

/**
 * This class storres the alias information of POJO's properties.
 * 
 * @author Consulting & Development. Aimar Tellitu - 04-may-2007
 * @since 1.0
 * 
 */
public class AliasEntry implements Comparable {

	private Type type;
	
	private String alias;
	
	private String hibernate;

	/**
	 * The Constructor.
	 * 
	 * @param type the type
	 * @param hibernate the hibernate
	 * @param alias the alias
	 */
	public AliasEntry(String alias, String hibernate, Type type) {
		this.type = type;
		this.alias = alias;
		this.hibernate = hibernate;
	}

	/**
	 * Gets the alias.
	 * 
	 * @return the alias
	 */
	public String getAlias() {
		return alias;
	}

	/**
	 * Sets the alias.
	 * 
	 * @param alias the alias
	 */
	public void setAlias(String alias) {
		this.alias = alias;
	}

	/**
	 * Gets the hibernate.
	 * 
	 * @return the hibernate
	 */
	public String getHibernate() {
		return hibernate;
	}

	/**
	 * Sets the hibernate.
	 * 
	 * @param hibernate the hibernate
	 */
	public void setHibernate(String hibernate) {
		this.hibernate = hibernate;
	}

	/**
	 * Gets the access path.
	 * 
	 * @return the access path
	 */
	public String getAccessPath() {
		int pos = this.hibernate.indexOf('.');
		return this.hibernate.substring(pos+1);
	}
	
	/**
	 * Gets the type.
	 * 
	 * @return the type
	 */
	public Type getType() {
		return type;
	}
	
	public int compareTo(Object o) {
		return this.alias.compareTo( ((AliasEntry) o).getAlias() );
	}

}