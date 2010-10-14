package com.code.aon.jaas.client.ast.core;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import com.code.aon.jaas.client.ast.IDataSourceMetaData;
import com.code.aon.jaas.client.ast.IDomainApplication;
import com.code.aon.jaas.client.ast.INodeVisitor;
import com.code.aon.jaas.client.ast.IRelation;

/**
 * This class represents an Application inside a Domain.
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 21-ago-2006
 * @since 1.0
 *  
 */
public class DomainApplication implements IDomainApplication {

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
	private static final long serialVersionUID = -4626066164962027557L;

	/** Application identifier. */
	private String id;

    /** Application DataSource meta data. */
	private IDataSourceMetaData metadata;

    /** Domain Profiles with its Roles. */
    private Map<String, IRelation> profiles = new LinkedHashMap<String, IRelation>();

    /** Domain Users with its Profiles. */
	private Map<String, IRelation> users = new HashMap<String, IRelation>();

	/**
     * Domain application identifier.
     * 
     * @param string
     */
	public void setId(String string) {
		this.id = string;
	}

    /**
     * Assign the data source meta data.
     * 
     * @param metadata
     */
	public void setDataSourceMetaData(IDataSourceMetaData metadata) {
		this.metadata = metadata;
	}

    /**
     * Adds a new relation of Profile with its Roles.
     * 
     * @param relation, <code>IRelation</code>
     */
    public void addProfile(IRelation relation) {
        if (!this.profiles.containsKey(relation)) {
        	this.profiles.put( relation.getId(), relation );
        }
    }

	/**
     * Adds a new relation of User with its Profiles.
	 * 
	 * @param relation
	 */
	public void addUser(IRelation relation) {
		if ( !this.users.containsKey(relation.getId()) ) {
			this.users.put( relation.getId(), relation );
		}
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.client.ast.INode#accept(com.code.aon.jaas.client.ast.INodeVisitor)
	 */
	public void accept(INodeVisitor visitor) {
		visitor.visitDomainApplication(this);
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.client.ast.INode#getId()
	 */
	public String getId() {
		return this.id;
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.client.ast.IDomainApplication#getDataSourceMetaData()
	 */
	public IDataSourceMetaData getDataSourceMetaData() {
		return this.metadata;
	}

	/*
     * (non-Javadoc)
     * 
     * @see com.code.aon.jaas.client.ast.IDomainApplication#profiles()
     */
    public Collection<IRelation> profiles() {
        return Collections.unmodifiableCollection( this.profiles.values() );
    }

    /*(non-Javadoc)
     * @see com.code.aon.jaas.client.ast.IDomainApplication#getProfile(java.lang.String)
     */
    public IRelation getProfile(String name) {
        return this.profiles.get(name);
    }

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.client.ast.IDomainApplication#updateProfile(com.code.aon.jaas.client.ast.IRelation)
	 */
	public IRelation updateProfile(IRelation relation) {
		return this.profiles.put( relation.getId(), relation );
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.client.ast.IDomainApplication#removeProfile(com.code.aon.jaas.client.ast.IRelation)
	 */
	public void removeProfile(IRelation relation) {
    	this.profiles.remove( relation.getId() );
	}

	/*(non-Javadoc)
	 * @see com.code.aon.jaas.client.ast.IDomainApplication#users()
	 */
	public Collection<IRelation> users() {
		return Collections.unmodifiableCollection( this.users.values() );
	}

	/*(non-Javadoc)
	 * @see com.code.aon.jaas.client.ast.IDomainApplication#getUser(java.lang.String)
	 */
	public IRelation getUser(String name) {
		return this.users.get(name);
	}

	/*(non-Javadoc)
	 * @see com.code.aon.jaas.client.ast.IDomainApplication#isProfileInUsers(java.lang.String)
	 */
	public boolean isProfileInUsers(String profile) {
		Iterator<IRelation> iter = users.values().iterator();
		while (iter.hasNext()) {
			IRelation relation = iter.next();
			Iterator<String> iterator = relation.relations().iterator();
			while (iterator.hasNext()) {
				if ( iterator.next().equals(profile) ) {
					return true;
				}
			}
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.client.ast.IDomainApplication#updateUser(com.code.aon.jaas.client.ast.IRelation)
	 */
	public IRelation updateUser(IRelation relation) {
		IRelation _relation = this.users.get( relation.getId() );
		this.users.put( relation.getId(), relation );
	    return (_relation == null)? relation: _relation;
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.client.ast.IDomainApplication#removeUser(java.lang.String)
	 */
	public void removeUser(IRelation relation) {
		this.users.remove( relation.getId() );
	}

}