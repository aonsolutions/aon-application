package com.code.aon.jaas.client.ast;

import java.util.Collection;

/**
 * Application that domain belongs to.
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 21-ago-2006
 * @since 1.0
 *
 */
public interface IDomainApplication extends INode {

    /**
     * Return domain datasource metadata.
     * 
     * @return IDataSourceMetaData
     */
    IDataSourceMetaData getDataSourceMetaData();

	/**
     * Return application defined profiles.
     * 
     * @return Collection
     */
	Collection<IRelation> profiles();

    /**
     * Return an application profile.
     * 
     * @param name String
     * @return IRelation
     */
	IRelation getProfile(String name);

    /**
     * Update the application existing profile, otherwise adds it. Return the previous profile.
     * 
     * @param relation
     * @return
     */
	IRelation updateProfile(IRelation relation);

    /**
     * Remove the profile.
     * 
     * @param relation
     */
	void removeProfile(IRelation relation);

    /**
     * Tell if one of existing user has selected profile.
     * 
     * @param profile String
     * @return boolean
     */
    boolean isProfileInUsers(String profile);

    /**
     * Return a collection of users.
     * 
     * @return Collection
     */
    Collection<IRelation> users();

    /**
     * Return the user.
     * 
     * @param name String
     * @return IRelation
     */
    IRelation getUser(String name);

    /**
     * Update the application existing user, otherwise adds it. Return the previous user.
     * 
     * @param relation
     * @return
     */
    IRelation updateUser(IRelation relation);

    /**
     * Remove the user and profiles that belongs to.
     * 
     * @param relation
     * @return
     */
    void removeUser(IRelation relation);

}


