package com.code.aon.jaas.client.ast;

import java.util.Collection;
import java.util.Map;

/**
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 20-jul-2006
 * @since 1.0
 *
 */

public interface IDomain extends INode {

	/** Default domain IP. */
	public static final String DEFAULT_DOMAIN_IP = "127.0.0.1";
	/** Default domain name. */
	public static final String DEFAULT_DOMAIN_NAME = "localhost";

	/**
	 * Return domain access policy.
	 * 
	 * @return IAccessPolicy
	 */
	IAccessPolicy getAccessPolicy();

	/**
	 * Set the access policy.
	 * 
	 * @param access
	 */
	void setAccessPolicy(IAccessPolicy access);

    /**
     * Return the data source meta data for all domain deployed applications.
     * 
     * @return IDataSourceMetaData
     */
	IDataSourceMetaData getDataSourceMetaData();

    /**
     * Set the data source meta data.
     * 
     * @param dsmt
     */
	void setDataSourceMetaData(IDataSourceMetaData dsmt);

	/**
     * Return doamin applications.
     * 
     * @return Collection
     */
    Collection<IDomainApplication> applications();

    /**
     * Return a domain application.
     * 
     * @param name
     * @return
     */
    IDomainApplication getDomainApplication(String name);

    /**
     * Remove a domain application.
     * 
     * @param appName
     * @return TODO
     */
	IDomainApplication remove(String appName);

	/**
     * Return domain available users.
     * 
     * @return Map
     */
	Map<String, IUser> standaloneUsers();

    /**
     * Return a domain available user.
     * 
     * @param name String
     * @return IUser
     */
	IUser getStandaloneUser(String name);

    /**
     * Add a domain user.
     * 
     * @param user
     * @return
     * @throws UserAlreadyExistException if user already exist.
     */
	void add(IUser user) throws UserAlreadyExistException;

	/**
     * Update domain user, if user does not exist then a new one is created.
     * 
     * @param user
     * @param oldUserId
     * @return previous user, or <tt>null</tt> if there was no user.
     * @throws UserAlreadyExistException if user already exist.
     */
	IUser update(IUser user, String oldUserId) throws UserAlreadyExistException;

    /**
     * Remove domain user.
     * 
     * @param user
     */
	void remove(IUser user);

}
